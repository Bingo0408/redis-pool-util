import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis pool util
 */
public class RedisPoolUtil {

    private static JedisPool jedisPool = null;

    private static ThreadLocal<Jedis> local = new ThreadLocal();

    /**
     * Private Constructor make it illegal to initialize instance
     */
    private RedisPoolUtil() {}


    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(2000);                       // max active conn nums
        config.setMaxIdle(10000);                       // max idle conn nums
        config.setTestOnBorrow(true);                   // whether check legality when get conn from pool
        config.setTimeBetweenEvictionRunsMillis(30000);
        config.setMinEvictableIdleTimeMillis(30000);
        jedisPool = new JedisPool(config, "127.0.0.1", 6379,10000);
    }

    /**
     * Get one jedis conn from JedisPool
     * @return jedis conn
     */
    public static Jedis getConnection() {
        Jedis jedis = local.get();
        if(jedis == null) {
            if(jedisPool == null) {
                initPool();
            }
            jedis = jedisPool.getResource();
            local.set(jedis);
        }
        return jedis;
    }

    /**
     * Return jedis conn to JedisPool
     * @return jedis conn
     */
    public static void closeConnection() {
        Jedis jedis = local.get();
        if(jedis != null)
            jedis.close();
        local.set(null);
    }

    /**
     * shutdown JedisPool
     */
    public static void shutdownPool() {
        if(jedisPool != null) {
            jedisPool.close();
        }
        System.out.println("Redis pool shutdown.");
    }
}
