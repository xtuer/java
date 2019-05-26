import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    // 求 x 的 n 次方
    static int foo(int x, int n) {
        if (n == 0) { return 1; }
        return foo(x*x, n/2) * (n%2==0 ? 1 : x);
    }

    static int pow(int x, int n, int t) {
        if (n == 0) { return 1; }
        if (n == 1) { return x * t; }
        return pow(x*x, n/2, t*(n%2==0 ? 1 : x));
    }

    public static void main(String[] args) throws Exception {
        System.out.println(pow(3, 1, 1));
        System.out.println(pow(3, 4, 1));
        System.out.println(pow(3, 5, 1));
        System.out.println(pow(3, 11, 1));
        System.out.println(foo(3, 5));
        System.out.println(foo(3, 11));
    }
}
