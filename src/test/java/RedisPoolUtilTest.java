import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisPoolUtilTest {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(3000);
        for(int i=0; i<2010; i++) {
            es.submit(new ClientThread(i));
        }
        es.shutdownNow();
        es.shutdown();
    }
}

class ClientThread implements Runnable {
    int i;
    public ClientThread(int i) {
        this.i = i;
    }
    public void run() {
        Jedis jedis= RedisPoolUtil.getConnection();
        jedis.set("key"+i, i+"");
        try {
//            Thread.currentThread().sleep((int)(Math.random()*1000));
            String foo = jedis.get("key"+i);
            System.out.println("key:" + foo + " 第:"+i+"个线程");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            RedisPoolUtil.closeConnection();
        }
    }
}
