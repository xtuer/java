import javax.crypto.Mac;
import java.security.NoSuchAlgorithmException;

public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        Mac mac = Mac.getInstance("HmacSHA1");
        System.out.println(mac);
    }
}
