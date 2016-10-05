import com.github.scribejava.apis.WeiXinApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class OAuthTest {
    @Test
    public void test() throws Exception {
        final String apiKey = "101292272";
        final String apiSecret = "5bdbe9403fcc3abe8eba172337904b5a";
        final OAuth20Service service = new ServiceBuilder().apiKey(apiKey).apiSecret(apiSecret)
                .callback("http://qbclsh.ngrok.cc/weixin/callback/qq")
                .scope("get_user_info").build(WeiXinApi.instance());
        System.out.println(service.getAuthorizationUrl());
    }

    @Test
    public void encrypt() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 每次生成的密码都不一样
        System.out.println(passwordEncoder.encode("Passw0rd"));
        for (int i = 0; i < 5; ++i) {
            String encryptedPassword = passwordEncoder.encode("Passw0rd");
            System.out.println(encryptedPassword);
            System.out.println(passwordEncoder.matches("Passw0rd", encryptedPassword)); // true
            System.out.println(passwordEncoder.matches("Password", encryptedPassword)); // false
        }
    }
}
