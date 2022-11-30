package com.buzz.redis.stats;

import io.pkts.buffer.Buffer;
import io.pkts.packet.IPPacket;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.protocol.Protocol;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import static com.buzz.redis.stats.FormatUtils.humanReadableByteSize;

/**
 * @author bairen
 * @description
 **/
public class RedisTrafficParser {
    private Set<String> clients = new HashSet<>();
    private String server;
    private Set<String> participants = new HashSet<>();

    //按照client分组保存
    private Map<String/*client*/, Deque<Command>> clientCommandMap = new HashMap<>();
    //集群之间的Command
    private Deque<Command> participantCommands = new ArrayDeque<>();

    private AtomicLong beginTs = new AtomicLong(Long.MAX_VALUE);
    private AtomicLong endTs = new AtomicLong();
    private int noCount = 0;//记录所有
    private int requestCount = 0;//只记录有数据传输的
    private long inBytes = 0;
    private long outBytes = 0;
    private String source;
    private String destination;
    private Option option;
    private long discardResponseBytes;//这里记录无法找到key的流量

    public RedisTrafficParser(Option option) {
        this.option = option;
    }

    private boolean isDestinationRedis(TCPPacket tcpPacket) {
        String port = String.valueOf(tcpPacket.getDestinationPort());
        //63开头，长度为4说明是redis端口
        return port.length() == 4 && port.startsWith("63");
    }

    private void setServer(String source) {
        if (server == null)
            server = source;
    }

    public boolean process(Packet packet) throws IOException {
        if (!packet.hasProtocol(Protocol.TCP)) {
            return true;
        }
        ++noCount;
        TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
        //skip if packet don't ship with any payload
        if (tcpPacket.getPayload() == null) {
            return true;
        }
        IPPacket ipPacket = tcpPacket.getParentPacket();
        //必须区分端口号，否则数据不准
        source = ipPacket.getSourceIP() + ":" + tcpPacket.getSourcePort();
        destination = ipPacket.getDestinationIP() + ":" + tcpPacket.getDestinationPort();
        int payloadLength = tcpPacket.getPayload().getArray().length;
        if (tcpPacket.getArrivalTime() < beginTs.get()) {
            beginTs.set(tcpPacket.getArrivalTime());
        }
        if (tcpPacket.getArrivalTime() > endTs.get()) {
            endTs.set(tcpPacket.getArrivalTime());
        }
        //System.out.println(noCount + "\t" + tcpPacket.getArrivalTime() + "\t" + ipPacket.getSourceIP()+":"+tcpPacket.getSourcePort() + "\t" + ipPacket.getDestinationIP()+":"+tcpPacket.getDestinationPort()+  "\t" + tcpPacket.getPayload().getArray().length);
        boolean incoming = isDestinationRedis(tcpPacket);
        if (incoming) {
            ++requestCount;
            inBytes += payloadLength;
            setServer(destination);
            clients.add(source);
            processRequest(source, tcpPacket);
        } else {
            outBytes += payloadLength;
            String redisHostPrefix = source.substring(0,source.lastIndexOf("."));
            String destHostPrefix = destination.substring(0,destination.lastIndexOf("."));
            //如果是同一个网段加入participants
            if (redisHostPrefix.equals(destHostPrefix)) {
                participants.add(destination);
                processHorizontal(source, destination, tcpPacket);
            } else {
                if(!processResponse(destination, tcpPacket)){
                   //System.err.println(noCount+" not found client! client="+destination+", payloadLength="+payloadLength);
                    discardResponseBytes+=payloadLength;
                }
            }
        }
        return true;
    }

    private void processRequest(String client, TCPPacket tcp) throws IOException {
        Queue queue = clientCommandMap.computeIfAbsent(client, (c) -> {
            return new ArrayDeque<>();
        });
        Command command = new Command();
        Buffer buffer = tcp.getPayload();
        int ret = resolvePacket(buffer, new Callback() {
            @Override
            public void invoke(String[] arrays) {
                command.setName(arrays[2]);
                command.setKey(arrays[4]);
                command.setReqBytes(buffer.getRawArray().length);
            }
        });
        switch (ret) {
            case 0: {
                queue.add(command);
                break;
            }
            case 1: {
                Command last = clientCommandMap.get(client).peekLast();
                last.setReqBytes(last.getReqBytes() + buffer.getArray().length);
            }
        }
    }

    private int resolvePacket(Buffer buffer, Callback callback) throws IOException {
        byte b = buffer.readByte();
        int ret = -1;
        if (b == '$') { //Bulk Strings
            //ignore
            return ret;
        } else if (b == '+') {// String
            //ignore
            return ret;
        } else if (b == '*') { //Arrays
            String value = buffer.toString();
            String[] arrays = value.split("\r\n");
            if (arrays.length < 3) {
                //ignore
                return ret;
            }
            String opt = arrays[2];
            if (opt.equals("QUIT") || opt.equals("PING")) {
                //ignore
                return ret;
            } else if (arrays.length < 4) {
                //ignore
                return ret;
            } else {
                callback.invoke(arrays);
//                cmd.setName(arrays[2]);
//                cmd.setKey(arrays[4]);
//                cmd.setReqBytes(buffer.getRawArray().length);
                ret = 0;
            }
        } else {
            // 这种情况是数据被拆包,追加last command
            ret = 1;
        }
        return ret;
    }

    private boolean processResponse(String client, TCPPacket tcpPacket) throws IOException {
        //说明包中没有包含之前client端请求的数据，这样我们就无法知道是哪个key
        if (!clientCommandMap.containsKey(client)) {
            return false;
        }
        Deque<Command> queue = clientCommandMap.get(client);
        Command last = queue.peekLast();
        if (last == null) {
            return false;
        }
        last.setResBytes(last.getResBytes() + tcpPacket.getPayload().getArray().length);
        return true;
    }

    private void processHorizontal(String source, String destination, TCPPacket tcp) throws IOException {
        Command command = new Command();
        Buffer buffer = tcp.getPayload();
        int ret = resolvePacket(buffer, new Callback() {
            @Override
            public void invoke(String[] arrays) {
                command.setName(arrays[2]);
                command.setKey(arrays[4]);
                command.setResBytes(buffer.getRawArray().length);
            }
        });
        //System.out.println("processHorizontal " + source + "=>" + destination + "\t" + noCount + "\t" + ret);
        switch (ret) {
            case 0: {
                //对于集群内只有出口流量
                participantCommands.add(command);
                break;
            }
            case 1: {
                Command last = participantCommands.peekLast();
                if (last != null) {
                    last.setResBytes(last.getReqBytes() + buffer.getArray().length);
                }
                break;
            }
        }
    }

    public void check() {
        List<Command> commandList = clientCommandMap.values()
                .stream()
                .flatMap(Deque::stream)
                .collect(Collectors.toList());
        for (Command command : commandList) {
            if (command.getName().equals("exists") && command.getResBytes() > 100) {
                System.out.println("find big EXISTS");
            }
        }
    }

    private String formatTime(long ts) {
        return FormatUtils.toDateTimeMillisString(ts / 1000);
    }

    private int costSec() {
        return Math.round((endTs.get() - beginTs.get()) / 1000000.0f);
    }

    private int requestQps() {
        return requestCount / costSec();
    }

    private long traffic(Collection<Command> commandList, boolean flag) {
        long inBytes = 0;
        long outBytes = 0;
        for (Command command : commandList) {
            inBytes += command.getReqBytes();
            outBytes += command.getResBytes();
        }
        return flag ? inBytes : outBytes;
    }

    private Map<String, CommandSummary> sort(Map<String, CommandSummary> map, Comparator<? super Map.Entry<String, CommandSummary>> comparator) {
        map = map.entrySet()
                .stream()
                .sorted(comparator)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        return map;
    }

    private Map<String, CommandSummary> sort(Map<String, CommandSummary> map) {
        return sort(map, Map.Entry.comparingByValue(Comparator.reverseOrder()));
    }


    public void printResult() {
        List<Command> commandList = clientCommandMap.values()
                .stream()
                .flatMap(Queue::stream)
                .collect(Collectors.toList());
        if(discardResponseBytes>0){
            System.err.println("因pcap文件数据不全，无法确定key的response流量: "+FormatUtils.humanReadableByteSize(discardResponseBytes));
        }
        print("## Summary");
        print("* Duration:");
        print(" * %s - %s (%ds)", formatTime(beginTs.get()), formatTime(endTs.get()), costSec());
        print("* In Traffic:");

        print(" * %s bytes (%s bytes/sec)", FormatUtils.humanReadableByteSize(inBytes), FormatUtils.humanReadableByteSize(inBytes / costSec()));
        print("* Out Traffic:");
        print(" * %s bytes (%s bytes/sec)", FormatUtils.humanReadableByteSize(outBytes), FormatUtils.humanReadableByteSize(outBytes / costSec()));
        print("* Total Requests:");
        print(" * %d requests (Avg %d req/sec)", requestCount, requestQps());
        print("");

        //按client分组
        if (option.isDetail()) {
            Map<String/*client*/, CommandSummary> clientSummaryMap = clientCommandMap.entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> e.getKey(), e -> reduce(e.getKey(), e.getValue())));
            //排序
            clientSummaryMap = sort(clientSummaryMap);
            TextTable table = new TextTable("client", "count", "in", "out");
            int n = 0;
            for (Map.Entry<String, CommandSummary> entry : clientSummaryMap.entrySet()) {
                table.addRow(entry.getKey(),
                        String.valueOf(entry.getValue().getCount()),
                        FormatUtils.humanReadableByteSize(entry.getValue().getInBytes()),
                        FormatUtils.humanReadableByteSize(entry.getValue().getOutBytes())
                );
                if (n > 9) break;
                n++;
            }
            print("## Client Detail (%d connection)", clients.size());
            print("");
            print(table.toString());
        }
        //集群之间的命令
        if (!participantCommands.isEmpty() && option.isDetail()) {

            Map<String/*cmd name*/, List<Command>> commandMap = participantCommands.stream().collect(Collectors.groupingBy(Command::getName));
            Map<String, CommandSummary> commandSummaryMap = commandMap.entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> e.getKey(), e -> reduce(e.getKey(), e.getValue())));
            commandSummaryMap = sort(commandSummaryMap);
            TextTable table = new TextTable("command", "count", "out");
            for (Map.Entry<String, CommandSummary> entry : commandSummaryMap.entrySet()) {
                table.addRow(entry.getKey(),
                        String.valueOf(entry.getValue().getCount()),
                        FormatUtils.humanReadableByteSize(entry.getValue().getOutBytes())
                );
            }
            print("## Cluster Inner Command Detail (%d)", participantCommands.size());
            print("");
            print(table.toString());

        }

        //按命令分组
        Map<String/*cmd name*/, List<Command>> commandMap = commandList.stream().collect(Collectors.groupingBy(Command::getName));
        Map<String, CommandSummary> commandSummaryMap = commandMap.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> reduce(e.getKey(), e.getValue())));
        commandSummaryMap = sort(commandSummaryMap);
        TextTable table = new TextTable("command", "count", "in", "out");
        for (Map.Entry<String, CommandSummary> entry : commandSummaryMap.entrySet()) {
            table.addRow(entry.getKey(),
                    String.valueOf(entry.getValue().getCount()),
                    FormatUtils.humanReadableByteSize(entry.getValue().getInBytes()),
                    FormatUtils.humanReadableByteSize(entry.getValue().getOutBytes())
            );
        }
        print("## Command Detail (%d)", commandList.size());
        print("");
        print(table.toString());

        for (Map.Entry<String, CommandSummary> entry : commandSummaryMap.entrySet()) {
            String cmdName = entry.getKey();
            commandDetail(commandMap, cmdName);
        }

    }

    private CommandSummary reduce(String name, Collection<Command> commandList) {
        return new CommandSummary(name,
                commandList.size(),
                traffic(commandList, true),
                traffic(commandList, false));
    }

    private void commandDetail(Map<String/*cmdName*/, List<Command>> commandMap, String cmdName) {
        print("## %s Detail (%d)", cmdName.substring(0, 1).toUpperCase() + cmdName.substring(1), commandMap.get(cmdName).size());
        print("");
        List<Command> commandList = commandMap.get(cmdName);
        //group
        Map<String/*key*/, List<Command>> commandKeyMap = commandList.stream().collect(Collectors.groupingBy(Command::getKey));
        //reduce
        Map<String/*key*/, CommandSummary> commandSummaryMap = commandKeyMap.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> reduce(e.getKey(), e.getValue())));
        //sort by count https://stackoverflow.com/questions/28607191/how-to-use-a-java8-lambda-to-sort-a-stream-in-reverse-order
        //commandSummaryMap = sort(commandSummaryMap,(f1,f2)->f2.getValue().getCount().compareTo(f1.getValue().getCount()));
        commandSummaryMap = sort(commandSummaryMap);
        int n = 0;
        TextTable table = new TextTable("No", "key", "count", "qps", "in", "out");
        for (Map.Entry<String, CommandSummary> entry : commandSummaryMap.entrySet()) {
            int count = entry.getValue().getCount();
            table.addRow(
                    String.valueOf(n),
                    entry.getKey(),
                    String.valueOf(count),
                    String.valueOf(count / costSec()),
                    FormatUtils.humanReadableByteSize(entry.getValue().getInBytes()),
                    FormatUtils.humanReadableByteSize(entry.getValue().getOutBytes())
            );
            if (n > 9) break;
            n++;
        }
        System.out.println(table.toString());
    }

    private void print(String format, Object... args) {
        print(String.format(format, args));
    }

    private void print(String line) {
        System.out.println(line);

    }

    @Data
    private static class Command {
        private String name;
        private String key;
        private long reqBytes;
        private long resBytes;


        @Override
        public String toString() {
            return String.format("Command(name=%s, key=%s, reqBytes=%s, resBytes=%s)",
                    name,
                    key,
                    FormatUtils.humanReadableByteSize(reqBytes),
                    FormatUtils.humanReadableByteSize(resBytes)
            );
        }
    }

    @Data
    @AllArgsConstructor
    private static class CommandSummary implements Comparable<CommandSummary> {
        private String name;//名称
        private Integer count;//执行次数
        private Long inBytes;//入口流量
        private Long outBytes;//出口流量

        @Override
        public int compareTo(CommandSummary o) {
            return outBytes.compareTo(o.outBytes);
        }

        public static final Comparator<CommandSummary> SORT_BY_COUNT = new Comparator<CommandSummary>() {

            @Override
            public int compare(CommandSummary o1, CommandSummary o2) {
                return o1.count.compareTo(o2.count);
            }
        };
    }

    private static interface Callback {
        public void invoke(String[] arrays);
    }


}
