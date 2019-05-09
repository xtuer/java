public class Test {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            for (int i = 0; i < 10; ++i) {
                System.out.println(i);
                if (i > 3) {
                    throw new RuntimeException("Exception occurred");
                }
            }
        });

        // 如果不设置，就会把异常抛出给 main() 函数: 设置为 null 也可以
        t.setUncaughtExceptionHandler((or, e) -> {
            System.out.println(or.getName());
        });

        t.start();
    }
}
