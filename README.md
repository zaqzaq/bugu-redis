bugu-redis
==========
BuguRedis是一个操作Redis的Java类库，它封装了Redis的各种常用功能，简单易用，并且支持Android系统。

功能介绍
----------
BuguRedis的功能包括3大部分：<br/>
1、各种Recipe。包括：
* StoreRecipe：存储<br/>
* CacheRecipe：缓存<br/>
* LockRecipe：锁<br/>
* CounterRecipe：计数器<br/>
* QueueRecipe：队列<br/>
* StackRecipe：栈<br/>

2、MQClient，用于发送消息。请参考[发送消息的说明](https://github.com/xbwen/bugu-redis/wiki/%E5%8F%91%E9%80%81%E6%B6%88%E6%81%AF%E7%9A%84%E8%AF%B4%E6%98%8E)<br/>
3、FileClient，用于发送文件。请参考[发送文件的说明](https://github.com/xbwen/bugu-redis/wiki/%E5%8F%91%E9%80%81%E6%96%87%E4%BB%B6%E7%9A%84%E8%AF%B4%E6%98%8E)<br/>

使用示例
----------
如何使用BuguRedis，请参考源代码中的单元测试部分。

    



