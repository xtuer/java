import javax.crypto.Mac;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        List<List<String>> list = new LinkedList<>();
        list.add(Arrays.asList("One", "Two", "Three"));
        list.add(new LinkedList<>());
        list.add(Arrays.asList("Alice", "Bob", "Carry"));

        list.stream().flatMap(List::stream).forEach(System.out::println);

    }
}
