import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        String text = null;//"Hello";
        Optional.ofNullable(text).map(String::toUpperCase).ifPresent(System.out::println);
    }
}
