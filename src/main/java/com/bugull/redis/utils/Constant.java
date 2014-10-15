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

package com.bugull.redis.utils;

/**
 * Constants for buguMQ framework.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class Constant {
    
    //default value for connection
    public static final int DEFAULT_PORT = 6379;
    public static final int DEFAULT_TIMEOUT = 5000;  //in milliseconds
    public static final int DEFAULT_DATABASE = 0;
    
    //some timeout value
    public static final int SUBSCRIBE_TIMEOUT = 10;  //in seconds
    public static final int BLOCK_POP_TIMEOUT = 30; //in seconds
    public static final int FILE_MSG_TIMEOUT = 30;  //in seconds
    
    public static final String MSG = "msg:";
    
    //for topic
    public static final String RETAIN = "retain:";
    
    //for online
    public static final String ONLINE = "online:";
    
    //for file
    public static final String FILE_CLIENT = "f:client:";
    
    public static final String FILE_CHUNKS = "f:chunks:";
    
    //for message
    public static final String EOF_MESSAGE = "_EOF_";
    
    public static final String SPLIT_MESSAGE = "_0x23_";
    public static final String SPLIT_EXTRA = "_0x3A_";
    
    //file message type
    public static final int FILE_REQUEST = 1;
    public static final int FILE_ACCEPT = 2;
    public static final int FILE_REJECT = 3;
    
    //file boradcast message type
    public static final byte BROADCAST_START = 0x10;
    public static final byte BROADCAST_END = 0x11;
    public static final byte BROADCAST_DATA = 0x12;

}
