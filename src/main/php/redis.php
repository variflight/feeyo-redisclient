<?php

/**
 * Created by PhpStorm.
 * User: Administrator
 * Date: 2017/6/12
 * Time: 18:14
 */

class redis
{
    /**
     * @var string
     */
    public $hostname = 'localhost';

    /**
     * @var int
     */
    public $port = 6379;

    /**
     * @var
     */
    public $unixSocket;

    /**
     * @var
     */
    public $password;

    /**
     * @var int
     */
    public $database = 0;

    /**
     * @var null
     */
    public $connectionTimeout = NULL;

    /**
     * @var null
     */
    public $dataTimeout = NULL;

    /**
     * @var int
     */
    public $socketClientFlags = STREAM_CLIENT_CONNECT;



    /**
     * @var resource redis socket connection
     */
    private $_socket = FALSE;


    /**
     * @param $name
     * @param $params
     * @return mixed
     */
    public function __call($name, $params)
    {
        $this->open();

        $redisCommand = strtoupper($name);

        return $this->executeCommand($redisCommand, $params);
    }

    /**
     * @throws \Exception
     */
    public function open()
    {
        if ($this->_socket !== FALSE) {
            return;
        }

        $connection = ($this->unixSocket ?: $this->hostname . ':' . $this->port) . ', database=' . $this->database;

        $this->_socket = @stream_socket_client(
            $this->unixSocket ? 'unix://' . $this->unixSocket : 'tcp://' . $this->hostname . ':' . $this->port,
            $errorNumber,
            $errorDescription,
            $this->connectionTimeout ? $this->connectionTimeout : ini_get('default_socket_timeout'),
            $this->socketClientFlags
        );

        if ($this->_socket) {
            if ($this->dataTimeout !== NULL) {
                stream_set_timeout($this->_socket, $timeout = (int)$this->dataTimeout, (int)(($this->dataTimeout - $timeout) * 1000000));
            }

            if ($this->password !== NULL) {
                $this->executeCommand('AUTH', [$this->password]);
            }

            if ($this->database !== NULL) {
                $this->executeCommand('SELECT', [$this->database]);
            }
        } else {
            $message = "Failed to open redis DB connection ($connection): $errorNumber - $errorDescription";
            throw new \Exception($message, $errorDescription, $errorNumber);
        }
    }

    /**
     * @param $name
     * @param array $params
     * @return mixed
     * @throws \Exception
     */
    public function executeCommand($name, $params = [])
    {
        $params = array_merge(explode(' ', $name), $params);
        $command = '*' . count($params) . "\r\n";

        foreach ($params as $arg) {
            $command .= '$' . mb_strlen($arg, '8bit') . "\r\n" . $arg . "\r\n";
        }

        fwrite($this->_socket, $command);

        return $this->parseResponse(implode(' ', $params));
    }

    /**
     * @param string $command
     * @return mixed
     * @throws \Exception on error
     */
    private function parseResponse($command)
    {
        if (($line = fgets($this->_socket)) === false) {
            throw new \Exception("Failed to read from socket.\nRedis command was: " . $command);
        }

        $type = $line[0]; $line = mb_substr($line, 1, -2, '8bit');

        switch ($type) {
            case '+': // Status reply
                if ($line === 'OK' || $line === 'PONG') {
                    return true;
                } else {
                    return $line;
                }
            case '-': // Error reply
                throw new \Exception("Redis error: " . $line . "\nRedis command was: " . $command);
            case ':': // Integer reply
                // no cast to int as it is in the range of a signed 64 bit integer
                return $line;
            case '$': // Bulk replies
                if ($line == '-1') {
                    return null;
                }
                $length = (int)$line + 2;
                $data = '';
                while ($length > 0) {
                    if (($block = fread($this->_socket, $length)) === false) {
                        throw new \Exception("Failed to read from socket.\nRedis command was: " . $command);
                    }
                    $data .= $block;
                    $length -= mb_strlen($block, '8bit');
                }

                return mb_substr($data, 0, -2, '8bit');
            case '*': // Multi-bulk replies
                $count = (int) $line;
                $data = [];
                for ($i = 0; $i < $count; $i++) {
                    $data[] = $this->parseResponse($command);
                }

                return $data;
            default:
                throw new \Exception('Received illegal data from redis: ' . $line . "\nRedis command was: " . $command);
        }
    }

}