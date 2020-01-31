import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

public class Test {
    public static int reverse(int a) {
        // 使用折半的方法进行逆转，并且使用过滤器去掉不需要的 bits
        int m16 = 0x0000ffff;
        int m8  = 0x00ff00ff;
        int m4  = 0x0f0f0f0f;
        int m2  = 0x33333333;
        int m1  = 0x55555555;

        a = ((a >> 16) & m16) | ((a & m16) << 16);
        a = ((a >> 8) & m8) | ((a & m8) << 8);
        a = ((a >> 4) & m4) | ((a & m4) << 4);
        a = ((a >> 2) & m2) | ((a & m2) << 2);
        a = ((a >> 1) & m1) | ((a & m1) << 1);

        return a;
    }

    public static String toBinary(int n) {
        return StringUtils.leftPad(Integer.toBinaryString(n), 32, '0');
    }

    public static void main(String[] args) {
        StopWatch sw = StopWatch.createStarted();
        int a = 29987;
        int b = reverse(a);
        int c = reverse(b);
        System.out.println(a);
        System.out.println(toBinary(a));
        System.out.println(toBinary(b));
        System.out.println(toBinary(c));
        System.out.println(c);
        System.out.println(sw.getTime());
    }
}
