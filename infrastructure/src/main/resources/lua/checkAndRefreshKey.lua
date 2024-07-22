-- 获取参数
local key = KEYS[1]
local value = ARGV[1]
local ttl = tonumber(ARGV[2])

-- 检查键是否存在
if redis.call("EXISTS", key) == 1 then
    -- 键存在，更新它的值并设置新的过期时间
    redis.call("SET", key, value)
    redis.call("PEXPIRE", key, ttl)
    return true
else
    -- 键不存在，设置键及其值，并设置过期时间
    redis.call("SET", key, value)
    redis.call("PEXPIRE", key, ttl)
    return true
end
