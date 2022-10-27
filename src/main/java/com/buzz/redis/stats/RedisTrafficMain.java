package com.buzz.redis.stats;

import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.packet.Packet;

import java.io.IOException;

/**
 * @author bairen
 * @description
 **/
public class RedisTrafficMain {

    public static void run(String path, String option) throws IOException {
        Pcap pcap = Pcap.openStream(path);
        RedisTrafficParser parser = new RedisTrafficParser(Option.valueOf(option));
        pcap.loop(new PacketHandler() {
            @Override
            public boolean nextPacket(Packet packet) throws IOException {
                return parser.process(packet);
            }
        });
        parser.printResult();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Require path argument");
        }
        String path = args[0];
        String option = args.length > 1 ? args[1] : null;
        run(path, option);
    }

}
