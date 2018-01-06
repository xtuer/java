import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.io.IOException;
import java.util.Date;

public class Test {
    public static void main(String[] args) throws IOException {
        String[] ips = "192.160,90".split(",");
        System.out.println(ips[0]);
    }
}
