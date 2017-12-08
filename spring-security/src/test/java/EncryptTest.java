import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EncryptTest {
    @Test
    public void encrypt() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = "Passw0rd";
        for (int i = 0; i < 5; ++i) {
            // 每次生成的密码都不一样
            String encryptedPassword = passwordEncoder.encode(password);
            System.out.println(encryptedPassword);
            System.out.println(passwordEncoder.matches(password, encryptedPassword)); // true
            System.out.println(passwordEncoder.matches("Password", encryptedPassword)); // false
        }
    }
}
