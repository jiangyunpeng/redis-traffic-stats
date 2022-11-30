# redis-traffic-stats

redis-traffic-stats is a analyzer for redis, likely [hirose31/redis-traffic-stats](https://github.com/hirose31/redis-traffic-stats), but implements by java

## Usage
```

tcpdump -n -s 0 tcp port 6394 -w redis.pcap -i bond0
tcpdump -n -s 0 tcp port 6390 -w redis.pcap -i bond0

sh redis-traffic-stats.sh redis.pcap
sh redis-traffic-stats.sh redis.pcap -d #detail
```

## Output
```
## Summary
* Duration:
 * 2022-10-10 10:12:09.084 - 2022-10-10 10:12:20.759 (12s)
* In Traffic:
 * 8.2MB bytes (699.2KB bytes/sec)
* Out Traffic:
 * 1.1GB bytes (90.9MB bytes/sec)
* Total Requests:
 * 226351 requests (Avg 18862 req/sec)

## Command Detail (81023)

command |count|in     |out   |
--------+-----+-------+------+
GET     |30129|3.6MB  |1.1GB |
REPLCONF|22   |2.1KB  |2.0MB |
EXISTS  |25589|3.4MB  |99.9KB|
EXPIRE  |3989 |500.0KB|80.2KB|
HEXISTS |14508|2.5MB  |56.7KB|
LPOP    |152  |18.8KB |46.7KB|
HGETALL |131  |17.0KB |34.7KB|
HGET    |82   |11.2KB |24.5KB|
DEL     |2103 |256.1KB|8.2KB |
SETNX   |1614 |225.6KB|6.3KB |
HSET    |1402 |412.1KB|5.5KB |
SETEX   |978  |1.3MB  |4.8KB |
INFO    |1    |77B    |4.0KB |
HDEL    |292  |30.0KB |1.1KB |
CLUSTER |1    |81B    |291B  |
RPUSH   |26   |18.7KB |112B  |
CONFIG  |1    |94B    |37B   |
INCRBY  |3    |327B   |24B   |

## GET Detail (30129)

No|key                                                                                       |count|qps|in     |out    |
--+------------------------------------------------------------------------------------------+-----+---+-------+-------+
0 |fund:fund:funddata:navprdc:codes                                                          |8273 |689|856.4KB|1.0GB  |
1 |fund:pd:daq_plv_ld_history_nav_chg:161601:161601:2                                        |1    |0  |124B   |1.5MB  |
2 |fund:pd:daq_plv_ld_history_nav_chg:217005:217005:1                                        |1    |0  |124B   |1.4MB  |
3 |fund:pd:daq_plv_ld_history_nav_chg:519100:519100:3                                        |1    |0  |124B   |1.1MB  |
4 |fund:pd:daq_plv_ld_fund_manager_info:80000248:pdFundManagerService.queryFundManagerByCode:|559  |46 |89.5KB |1.1MB  |
5 |fund:pd:daq_plv_ld_trustee_info:80001067:80001067                                         |2144 |178|257.5KB|1.0MB  |
6 |fund:pd:daq_plv_ld_fund_manager_info:80000222:pdFundManagerService.queryFundManagerByCode:|536  |44 |85.8KB |1.0MB  |
7 |fund:pd:daq_plv_ld_history_nav_chg:180013:180013:2                                        |1    |0  |124B   |1.0MB  |
8 |fund:pd:daq_plv_ld_history_nav_chg:519696:519696:4                                        |1    |0  |124B   |991.3KB|
9 |fund:pd:daq_plv_ld_history_nav_chg:320011:320011:3                                        |1    |0  |124B   |884.6KB|
10|fund:pd:daq_plv_lb_nav:003985:003985:singe:0                                              |830  |69 |95.6KB |884.3KB|

## REPLCONF Detail (22)

No|key|count|qps|in   |out  |
--+---+-----+---+-----+-----+
0 |ACK|22   |1  |2.1KB|2.0MB|

## EXISTS Detail (25589)

No|key                                                                                       |count|qps|in     |out  |
--+------------------------------------------------------------------------------------------+-----+---+-------+-----+
0 |fund:pd:daq_plv_ld_trustee_info:80001067:80001067                                         |2143 |178|263.7KB|8.4KB|
1 |fund:pd:daq_plv_ld_trustee_info:10000020:10000020                                         |1104 |92 |135.8KB|4.3KB|
2 |fund:pd:daq_plv_ld_trustee_info:80001120:80001120                                         |1049 |87 |129.1KB|4.1KB|
3 |fund:pd:daq_plv_lb_nav:003985:003985:singe:0                                              |830  |69 |98.1KB |3.2KB|
4 |fund:pd:daq_plv_lb_nav:675061:675061:singe:0                                              |639  |53 |75.5KB |2.5KB|
5 |fund:pd:daq_plv_lb_nav:006966:006966:singe:0                                              |607  |50 |71.7KB |2.4KB|
6 |fund:pd:daq_plv_ld_fund_manager_info:80000248:pdFundManagerService.queryFundManagerByCode:|560  |46 |91.3KB |2.2KB|
7 |fund:pd:daq_plv_ld_fund_manager_info:80000222:pdFundManagerService.queryFundManagerByCode:|536  |44 |87.4KB |2.1KB|
8 |fund:pd:daq_plv_lb_nav:001338:001338:singe:0                                              |486  |40 |57.4KB |1.9KB|
9 |fund:pd:daq_plv_lb_nav:012414:012414:singe:0                                              |479  |39 |56.6KB |1.9KB|
10|fund:pd:daq_plv_lb_nav:519760:519760:singe:0                                              |478  |39 |56.5KB |1.9KB|

## EXPIRE Detail (3989)

No|key                                            |count|qps|in    |out   |
--+-----------------------------------------------+-----+---+------+------+
0 |fund:fund:funddata:navprdc:lock:007246:20221010|1    |0  |132B  |64.7KB|
1 |fund:pd:25105                                  |152  |12 |15.1KB|604B  |
2 |fund:pd:894193                                 |134  |11 |13.5KB|536B  |
3 |fund:pd:735511                                 |43   |3  |4.3KB |172B  |
4 |fund:pd:22411                                  |41   |3  |4.1KB |164B  |
5 |fund:pd:28155                                  |29   |2  |2.9KB |116B  |
6 |fund:pd:97                                     |23   |1  |2.2KB |92B   |
7 |fund:pd:920422                                 |22   |1  |2.2KB |88B   |
8 |fund:pd:fundrank:type:20                       |21   |1  |2.3KB |84B   |
9 |fund:pd:1185729                                |21   |1  |2.1KB |84B   |
10|fund:pd:32431                                  |19   |1  |1.9KB |76B   |

## HEXISTS Detail (14508)

No|key                                                |count|qps|in     |out  |
--+---------------------------------------------------+-----+---+-------+-----+
0 |fund:pd:daq_plv_ld_trustee_info:80001121:new       |914  |76 |151.7KB|3.6KB|
1 |fund:pd:daq_plv_lb_nav:005928:new                  |714  |59 |107.4KB|2.8KB|
2 |fund:pd:daq_plv_lb_nav:006966:new                  |607  |50 |91.3KB |2.4KB|
3 |fund:pd:daq_plv_lb_nav:012414:new                  |478  |39 |71.9KB |1.9KB|
4 |fund:pd:daq_plv_lb_nav:519760:new                  |478  |39 |71.9KB |1.9KB|
5 |fund:pd:daq_plv_lb_nav:000662:new                  |425  |35 |63.9KB |1.7KB|
6 |fund:pd:daq_plv_ld_fund_executive_info:30053934:new|314  |26 |59.7KB |1.2KB|
7 |fund:pd:daq_plv_ld_fund_manager_info:80375536:new  |259  |21 |54.6KB |1.0KB|
8 |fund:pd:daq_plv_ld_fund_manager_info:80065990:new  |215  |17 |45.4KB |860B |
9 |fund:pd:daq_plv_ld_fund_executive_info:30488046:new|190  |15 |36.1KB |760B |
10|fund:pd:daq_plv_ld_fund_executive_info:30170018:new|190  |15 |36.1KB |760B |

## LPOP Detail (152)

No|key                                                 |count|qps|in    |out   |
--+----------------------------------------------------+-----+---+------+------+
0 |fund:pd:basic:delay:queue:key:pd-executiveInfo-queue|98   |8  |12.2KB|30.6KB|
1 |fund:fintech:marketsw:basic:delay:queue:key:foPush:2|48   |4  |6.0KB |15.5KB|
2 |fund:pd:basic:delay:queue:key:search-index-queue    |1    |0  |123B  |548B  |
3 |fund:basic:delay:queue:key:notice_queue             |5    |0  |570B  |25B   |

## HGETALL Detail (131)

No|key                                                       |count|qps|in  |out |
--+----------------------------------------------------------+-----+---+----+----+
0 |fund:pd:daq_plv_lb_basic_info_search_index:-2043891198:new|1    |0  |136B|324B|
1 |fund:pd:daq_plv_lb_basic_info_search_index:-1375560728:new|1    |0  |136B|324B|
2 |fund:pd:daq_plv_lb_basic_info_search_index:-1309808536:new|1    |0  |136B|324B|
3 |fund:pd:daq_plv_lb_basic_info_search_index:-1899385550:new|1    |0  |136B|324B|
4 |fund:pd:daq_plv_lb_basic_info_search_index:-1411013110:new|1    |0  |136B|324B|
5 |fund:pd:daq_plv_lb_basic_info_search_index:-1477278599:new|1    |0  |136B|324B|
6 |fund:pd:daq_plv_lb_basic_info_search_index:-1234038497:new|1    |0  |136B|324B|
7 |fund:pd:daq_plv_lb_basic_info_search_index:-1685267332:new|1    |0  |136B|324B|
8 |fund:pd:daq_plv_lb_basic_info_search_index:-1431617683:new|1    |0  |136B|324B|
9 |fund:pd:daq_plv_lb_basic_info_search_index:-1565214174:new|1    |0  |136B|324B|
10|fund:pd:daq_plv_lb_basic_info_search_index:-1660557574:new|1    |0  |136B|324B|

## HGET Detail (82)

No|key                                                  |count|qps|in   |out   |
--+-----------------------------------------------------+-----+---+-----+------+
0 |fund:market:slefchoose:selectyield:86736637:20220930 |73   |6  |9.9KB|21.4KB|
1 |fund:market:slefchoose:selectyield:200608363:20220930|8    |0  |1.1KB|2.3KB |
2 |fund:factor:scheme:schemeno:552022080516320000000093 |1    |0  |154B |751B  |

## DEL Detail (2103)

No|key                                              |count|qps|in  |out|
--+-------------------------------------------------+-----+---+----+---+
0 |fund:fund:funddata:quoteslock:930781.CSI:20221010|4    |0  |492B|16B|
1 |fund:fund:funddata:quoteslock:399967.SZ:20221010 |4    |0  |488B|16B|
2 |fund:fund:funddata:quoteslock:399001.SZ:20221010 |4    |0  |488B|16B|
3 |fund:fund:funddata:quoteslock:801141.SL:20221010 |4    |0  |488B|16B|
4 |fund:fund:funddata:quoteslock:801124.SL:20221010 |4    |0  |488B|16B|
5 |fund:fund:funddata:quoteslock:801112.SL:20221010 |4    |0  |488B|16B|
6 |fund:fund:funddata:quoteslock:801142.SL:20221010 |4    |0  |488B|16B|
7 |fund:fund:funddata:quoteslock:801178.SL:20221010 |4    |0  |488B|16B|
8 |fund:fund:funddata:quoteslock:000987.SH:20221010 |4    |0  |488B|16B|
9 |fund:fund:funddata:quoteslock:000852.SH:20221010 |4    |0  |488B|16B|
10|fund:fund:funddata:quoteslock:H30199.CSI:20221010|4    |0  |492B|16B|

## SETNX Detail (1614)

No|key                                              |count|qps|in  |out|
--+-------------------------------------------------+-----+---+----+---+
0 |fund:fund:funddata:quoteslock:930781.CSI:20221010|4    |0  |580B|16B|
1 |fund:fund:funddata:quoteslock:399967.SZ:20221010 |4    |0  |576B|16B|
2 |fund:fund:funddata:quoteslock:399001.SZ:20221010 |4    |0  |576B|16B|
3 |fund:fund:funddata:quoteslock:801141.SL:20221010 |4    |0  |576B|16B|
4 |fund:fund:funddata:quoteslock:801124.SL:20221010 |4    |0  |576B|16B|
5 |fund:fund:funddata:quoteslock:801112.SL:20221010 |4    |0  |576B|16B|
6 |fund:fund:funddata:quoteslock:801142.SL:20221010 |4    |0  |576B|16B|
7 |fund:fund:funddata:quoteslock:801178.SL:20221010 |4    |0  |576B|16B|
8 |fund:fund:funddata:quoteslock:000987.SH:20221010 |4    |0  |576B|16B|
9 |fund:fund:funddata:quoteslock:000852.SH:20221010 |4    |0  |576B|16B|
10|fund:fund:funddata:quoteslock:H30199.CSI:20221010|4    |0  |580B|16B|

## HSET Detail (1402)

No|key                     |count|qps|in    |out |
--+------------------------+-----+---+------+----+
0 |fund:pd:25105           |152  |12 |19.5KB|608B|
1 |fund:pd:894193          |135  |11 |18.9KB|536B|
2 |fund:pd:735511          |43   |3  |6.5KB |172B|
3 |fund:pd:22411           |41   |3  |5.9KB |164B|
4 |fund:pd:28155           |29   |2  |3.8KB |116B|
5 |fund:pd:97              |23   |1  |3.1KB |92B |
6 |fund:pd:920422          |22   |1  |3.2KB |88B |
7 |fund:pd:fundrank:type:20|21   |1  |29.0KB|84B |
8 |fund:pd:1185729         |21   |1  |3.1KB |84B |
9 |fund:pd:32431           |19   |1  |2.4KB |76B |
10|fund:pd:750769          |18   |1  |2.6KB |72B |

## SETEX Detail (978)

No|key                                                    |count|qps|in   |out|
--+-------------------------------------------------------+-----+---+-----+---+
0 |fund:fund:funddata:quotes:801110.SL:20221010:quotesNew |4    |0  |5.3KB|20B|
1 |fund:fund:funddata:quotes:801733.SL:20221010:quotesNew |4    |0  |5.3KB|20B|
2 |fund:fund:funddata:quotes:000016.SH:20221010:quotesNew |4    |0  |5.5KB|20B|
3 |fund:fund:funddata:quotes:H30205.CSI:20221010:quotesNew|4    |0  |5.5KB|20B|
4 |fund:fund:funddata:quotes:801731.SL:20221010:quotesNew |4    |0  |5.3KB|20B|
5 |fund:fund:funddata:quotes:801141.SL:20221010:quotesNew |4    |0  |5.3KB|20B|
6 |fund:fund:funddata:quotes:000993.SH:20221010:quotesNew |4    |0  |5.5KB|20B|
7 |fund:fund:funddata:quotes:H30202.CSI:20221010:quotesNew|4    |0  |5.5KB|20B|
8 |fund:fund:funddata:quotes:801037.SL:20221010:quotesNew |4    |0  |5.3KB|20B|
9 |fund:fund:funddata:quotes:801033.SL:20221010:quotesNew |4    |0  |5.3KB|20B|
10|fund:fund:funddata:quotes:931009.CSI:20221010:quotesNew|4    |0  |5.5KB|20B|

## INFO Detail (1)

No|key|count|qps|in |out  |
--+---+-----+---+---+-----+
0 |all|1    |0  |77B|4.0KB|

## HDEL Detail (292)

No|key                      |count|qps|in    |out |
--+-------------------------+-----+---+------+----+
0 |fund:pd:25105            |150  |12 |15.2KB|596B|
1 |fund:pd:894193           |108  |9  |11.1KB|432B|
2 |fund:pd:fundrank:type:20 |21   |1  |2.3KB |84B |
3 |fund:pd:fundrank:type:100|7    |0  |784B  |28B |
4 |fund:pd:fundrank:type:60 |6    |0  |666B  |24B |

## CLUSTER Detail (1)

No|key |count|qps|in |out |
--+----+-----+---+---+----+
0 |info|1    |0  |81B|291B|

## RPUSH Detail (26)

No|key                                                 |count|qps|in    |out |
--+----------------------------------------------------+-----+---+------+----+
0 |fund:fintech:marketsw:basic:delay:queue:key:foPush:2|26   |2  |18.7KB|112B|

## CONFIG Detail (1)

No|key|count|qps|in |out|
--+---+-----+---+---+---+
0 |get|1    |0  |94B|37B|

## INCRBY Detail (3)

No|key                   |count|qps|in  |out|
--+----------------------+-----+---+----+---+
0 |fund:basic:counter:key|3    |0  |327B|24B|

```