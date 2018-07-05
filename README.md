# feeyo-redisclient-ext

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
	3、你可以很平滑的接入到我们的多租户体系、监控体系

### 怎么使用
	1、联系OPS， 开通账户及Kafka队列服务
	2、通过 feeyo-redisclient-ext 中的工具类，使用 KPUSH & KPOP 就可以了
	
	
