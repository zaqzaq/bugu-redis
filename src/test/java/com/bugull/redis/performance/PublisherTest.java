/*
 * Copyright (c) www.bugull.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
