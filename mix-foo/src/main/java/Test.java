import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        List<String> list = Arrays.asList("1", "20", "3").stream().filter(s -> s.length() > 10).collect(Collectors.toList());
        System.out.println(list);
        System.out.println(Arrays.asList("1", "20", "3").stream().filter(s -> s.length() > 1).collect(Collectors.toSet()).getClass());
    }
}
