import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        System.out.println(tokens("H3C18"));
        System.out.println(tokens("H301C108"));
    }

    private static final Pattern PATTERN = Pattern.compile("[a-zA-Z]\\d+");

    public static List<String> tokens(String text) {
        List<String> result = new LinkedList<>();
        Matcher matcher = PATTERN.matcher(text);

        while (matcher.find()) {
            result.add(matcher.group(0));
        }

        return result;
    }
}
