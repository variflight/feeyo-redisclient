import redis

host = 'localhost' 
port = 8066
password = 'pwd05'


r = redis.Redis(host=host, port=port, password=password, decode_responses=True)

# kpush
args = ['test02','python_test']
resp1 = r.execute_command('kpush', *args)
print resp1

# kpop
resp2 = r.execute_command('kpop', "test02")
print resp2



