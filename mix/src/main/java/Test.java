import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.io.IOException;
import java.util.Date;

public class Test {
    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 10; ++i) {
            System.out.println(System.currentTimeMillis());
            String token = JWT.create().withClaim("username", "黄彪").withClaim("password", "Passw0rd")
                    .withExpiresAt(new Date()).withIssuedAt(new Date())
                    .withKeyId("MAGIC").sign(Algorithm.HMAC256("Secret"));
            System.out.println(token);
        }
    }
}
