import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(null, null);
        map.put(null, "One");
        System.out.println(map);
    }
}
