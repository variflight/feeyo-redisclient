<?php
/**
 * 本例测试样例，实际使用中应使用单例模式连接
 * Created by PhpStorm.
 * User: sunxia
 * Date: 2018/7/3
 * Time: 下午2:42
 */

include  __DIR__.DIRECTORY_SEPARATOR.'redis.php';

$redisObject = new redis();

$redisObject->hostname = '192.168.14.66';
$redisObject->port = 6379;
$redisObject->database = 0;

/* 如需要密码连接，给属性 password 赋值即可  */
//$redisObject->password = '123456';

/* kafuka 操作样例 */
$redisObject->kpush('opic001','xxxxxxxxx');
$redisObject->kpush('opic001',2,'xxxxxxx');

$redisObject->kpop('opic001');
$redisObject->kpop('opic001',2,3);


$redisObject->KPARTITIONS('opic001');
$redisObject->KOFFSET('opic001',2,time());

