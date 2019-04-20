import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    String name;
    int age;

    public static void main(String[] args) throws Exception {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, null);
        map.put(2, "Two");

        System.out.println(map);
    }
}
