public class ThreadLocalTest {
    private static ThreadLocal<String> foo = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            await(300);
            System.out.println(Thread.currentThread().getName() + ": " + foo.get()); // [2] 输出: Thread-1: null
            foo.set("1"); // [3]

            await(1000);
            System.out.println(Thread.currentThread().getName() + ": " + foo.get()); // [5] 输出: Thread-1: 1
        }, "Thread-1").start();

        new Thread(() -> {
            foo.set("2"); // [1]

            await(600);
            System.out.println(Thread.currentThread().getName() + ": " + foo.get()); //[4] 输出: Thread-2: 2
        }, "Thread-2").start();
    }

    public static void await(long timeout) {
        try { Thread.sleep(timeout); } catch (InterruptedException e) {}
    }
}
