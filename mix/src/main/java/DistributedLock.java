import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DistributedLock {
    public static void main(String[] args) throws Exception {
        testZooKeeperThread();
    }

    public static void testDistributedLock() throws Exception {
        // [1] This will create a connection to a ZooKeeper cluster using default values.
        // The only thing that you need to specify is the retry policy. For most cases, you should use:
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(10000, 3));

        // [2] The client must be started (and closed when no longer needed).
        client.start();

        InterProcessMutex lock = new InterProcessMutex(client, "/ebag/lock");

        // [3] 获取全局锁
        if (lock.acquire(10, TimeUnit.SECONDS)) {
            try {
                // [4] 业务代码
                System.out.println("Do something");
            } finally {
                // [5] 释放全局锁
                lock.release();
            }
        }

        // [6] Close the client
        client.close();
    }

    public static void testLocalThread() {
        List<Thread> threads = new LinkedList<>();

        for (int i = 0; i < 100; ++i) {
            threads.add(new Thread(new IncreaseRunnable()));
        }

        for (Thread thread : threads) {
            thread.start();
        }
    }

    public static void testZooKeeperThread() {
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(10000, 3));
        client.start();
        List<Thread> threads = new LinkedList<>();

        for (int i = 0; i < 100; ++i) {
            threads.add(new Thread(new ZooKeeperRunnable(client, "/ebag/lock")));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        // client.close();
    }
}

class IncreaseRunnable implements Runnable {
    public static int sn = 0;

    public void run() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sn++;
        System.out.println(sn);
    }
}

class ZooKeeperRunnable implements Runnable {
    public static int sn = 0;
    private InterProcessMutex lock;

    public ZooKeeperRunnable(CuratorFramework client, String path) {
        lock = new InterProcessMutex(client, path);
    }

    public void run() {
        try {
            // 获取全局锁
            if (lock.acquire(10, TimeUnit.SECONDS)) {
                try {
                    // Thread.sleep(100);
                    sn++;
                    System.out.println(sn);
                } finally {
                    // 释放全局锁
                    lock.release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
