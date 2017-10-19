import java.util.LinkedList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {
        List<String> strs = new LinkedList<>();
        strs.add("1");
        strs.add("2");
        strs.add("3");
        System.out.println(String.join(", ", strs));
    }
}
