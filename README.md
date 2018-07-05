# feeyo-kafkaclient

基于 redis 协议，扩展了几条指令，用于操作 kafka topic, 扩展指令如下
	KPUSH 			{topic}  { content  }
	KPUSH 			{topic}  { partition }  { content }
	KPOP 			{topic}
	KPOP 			{topic}  { partition }  { offset }
	KPARTITIONS 	{topic}	    	 				
	KOFFSET 		{topic}  { partition }  { time }	

