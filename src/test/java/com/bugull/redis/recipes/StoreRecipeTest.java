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

package com.bugull.redis.recipes;

import com.bugull.redis.RedisConnection;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class StoreRecipeTest {
    
    @Test
    public void test() throws Exception {
        RedisConnection conn = RedisConnection.getInstance();
        conn.setHost("192.168.0.200");
        conn.setPassword("foobared");
        conn.connect();
        
        String key = "x";
        String value = "y";
        
        StoreRecipe store = new StoreRecipe();
        
        //Assert.assertNull(store.get(key.getBytes()));
        
        store.set(key, value);
        
        for(int i=0; i<100; i++){
            store.set(key + i, value + i);
        }
        
        //Assert.assertEquals(value, store.get(key));
        
        conn.disconnect();
    }

}
