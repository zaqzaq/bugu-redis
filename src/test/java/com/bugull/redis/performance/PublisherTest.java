/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.redis.performance;

import com.bugull.redis.Connection;
import com.bugull.redis.mq.MQClient;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class PublisherTest {
    
    Connection conn;
    
    @Test
    public void testPublish() throws Exception {
        conn = Connection.getInstance();
        conn.setHost("192.168.0.200");
        conn.setPassword("foobared");
        conn.setClientId("publisher");
        conn.connect();
        
        for(int i=0; i<2000; i++){
            PublishTask task = new PublishTask(i);
            new Thread(task).start();
        }
        
        Thread.sleep(30L * 1000L);
        
        conn.disconnect();
    }
    
    class PublishTask implements Runnable {
        private int index;
        
        public PublishTask(int index){
            this.index = index;
        }

        @Override
        public void run() {
            try{
                MQClient client = conn.getMQClient();
                client.publish("topic", ("hello" + index).getBytes());
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        
    }

}
