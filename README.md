# feeyo-redisclient

基于 redis 协议，扩展了几条指令，用于操作 kafka topic

###  扩展指令如下
	KPUSH 			{topic}  { content  }
	KPUSH 			{topic}  { partition }  { content }
	KPOP 			{topic}
	KPOP 			{topic}  { partition }  { offset }
	KPARTITIONS 	{topic}	    	 				
	KOFFSET 		{topic}  { partition }  { time }	
	
### 为什么要使用
	1、redis 中的 list/set 满足不了真正的队列需求
	2、你一定会有场景需要堆积、需要再次消费、需要多人共同消费，总的来说使用场景很多
	3、通过  redis-ext 的访问，平滑的接入我们的多租户体系、监控体系，安全性更高

### 怎么使用
	1、联系OPS， 开通账户及 Kafka Topic
	2、通过 redis-ext 中的扩展指令即可操作 kafka 队列
	
	
