import java.net.URLEncoder;
import java.util.Base64;

public class Test {
    public static void main(String[] args) throws Exception {
        String c = Base64.getUrlEncoder().encodeToString("Google=+".getBytes());
        System.out.println(c);
        System.out.println(new String(Base64.getUrlDecoder().decode(c)));

        System.out.println(URLEncoder.encode(c, "UTF-8"));
    }
}
