import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Test {
    public static void main(String[] args) {
        Pair<String, String> pair = ImmutablePair.of("Alice", "Passw0rd");
        System.out.println(pair);
    }
}
