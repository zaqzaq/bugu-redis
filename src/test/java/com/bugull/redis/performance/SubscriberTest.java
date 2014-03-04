/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.redis.performance;

import com.bugull.redis.Connection;
import com.bugull.redis.listener.TopicListener;
import com.bugull.redis.mq.MQClient;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class SubscriberTest {
    
    int x;
    
    @Test
    public void testSubscribe() throws Exception {
        Connection conn = Connection.getInstance();
        conn.setHost("192.168.0.200");
        conn.setPassword("foobared");
        conn.setClientId("subscriber");
        conn.connect();
        
        
        MQClient client = conn.getMQClient();
        
        TopicListener listener = new TopicListener(){
            @Override
            public void onTopicMessage(String topic, byte[] message) {
                synchronized(this){
                    x++;
                    System.out.println(x);
                }
            }
        };
        
        client.setTopicListener(listener);
        
        client.subscribe("topic");
        
        Thread.sleep(60L * 1000L);
        
        conn.disconnect();
    }

}
