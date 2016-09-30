import java.util.HashMap;
import java.util.Map;

public class ImportExamRecords {
    public static void main(String[] args) {
        Map<String, Score> map = new HashMap<String, Score>();

        map.put("数学", new Score("1", "Biao"));
        map.put("数学", new Score("1", "Biao"));
        map.put("语文", new Score("3", "Alice"));
        System.out.println(JsonUtil.toJson(map));
    }
}
