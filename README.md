# feeyo-redisclient-ext

基于 redis 协议，扩展了几条指令，用于操作 kafka topic

###  扩展指令如下
	KPUSH 		{topic} {content }
	KPUSH 		{topic} {partition} {content}
	KPOP 		{topic}
	KPOP 		{topic} {partition} {offset}
	KPARTITIONS {topic}	    	 				
	KOFFSET 	{topic} {partition} {time}	
	
### 为什么要使用
	1、redis 中的 list/set 满足不了真正的队列需求
	2、你一定会有场景需要堆积、需要再次消费、需要多人共同消费，总的来说使用场景很多
	3、你可以很平滑的接入到我们的多租户体系、监控体系

### 怎么使用
	1、联系OPS， 开通账户及Kafka队列服务
	2、feeyo-redisclient-ext 已经含了jedis2.9的扩展, PHP & python 操作的工具类， 你可以使用他们扩展的 KPUSH & KPOP 指令
	
	
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
	
	
