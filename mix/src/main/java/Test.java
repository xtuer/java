import org.apache.commons.lang3.RandomStringUtils;

import java.util.Base64;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 20; ++i) {
            String src = RandomStringUtils.randomAscii(50);
            String s1 = Base64.getEncoder().encodeToString(src.getBytes());
            String s2 = Base64.getUrlEncoder().encodeToString(src.getBytes());
            System.out.println(s1);
            System.out.println(s2);
            System.out.println();
        }
    }
}
