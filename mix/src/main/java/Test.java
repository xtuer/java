import java.util.Base64;

public class Test {
    public static void main(String[] args) throws Exception {
        String payload = "{\"username\": \"Biao\", \"role\": \"ROLE_ADMIN\", \"signedAt\": 8823414213}";
        String c = Base64.getEncoder().encodeToString(payload.getBytes());
        System.out.println(c);
        System.out.println(new String(Base64.getDecoder().decode(c)));
    }
}
