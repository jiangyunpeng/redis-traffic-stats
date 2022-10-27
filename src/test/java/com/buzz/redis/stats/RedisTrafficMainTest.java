package com.buzz.redis.stats;
import org.junit.Test;

import java.io.IOException;

/**
 * @author bairen
 * @description
 **/
public class RedisTrafficMainTest {

    @Test
    public void test() throws IOException {
        String path = System.getProperty("user.home")+"/Downloads/redis.pcap";
        RedisTrafficMain.run(path,null);
    }
}
