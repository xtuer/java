public class Test {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            System.out.println("Hello");
        });

        t.start();
    }
}
