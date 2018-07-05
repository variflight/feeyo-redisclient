# feeyo-redisclient

基于 redis 协议，扩展了几条指令，用于操作 kafka topic

###  扩展指令如下
	KPUSH 		{topic} {content }
	KPUSH 		{topic} {partition} {content}
	KPOP 		{topic}
	KPOP 		{topic} {partition} {offset}
	KPARTITIONS {topic}	    	 				
	KOFFSET 	{topic} {partition} {time}	
	
### 为什么要使用
	1、Redis 中的 list 满足不了真正的队列需求，毕竟内存不是磁盘，
	简单的扩展几个指令，就可以满足我们的数据堆积、再次消费、多人共同消费 的需求
	
	2、平滑的接入到我们的多租户体系、监控体系

### 怎么使用
	1、联系OPS， 开通账户及Kafka队列服务
	2、feeyo-redisclient 已经含了jedis2.9的扩展、PHP 、Python 操作的工具类， 
	你可以使用这些扩展，也可以自己实现
	
	
### Demo
	redis xxxxx:8080> kpush test01 content
	1) "1"
	2) “4812903"
	
	redis xxxxx:8080> kpop test01
	1) “1" 
	2) "4299168"
	3) “content”

	redis xxxxx:8080> KOFFSET test01 1 1529729771000
	1) "4299170"
	2) “1529729788289"

	redis xxxxx:8080> KOFFSET test01 1 -1
	1) "4814078"
	2) "-1"
	
	
