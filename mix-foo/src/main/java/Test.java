import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) throws Exception {
        String text = "<a>Bob</a><span>Google</span>";
        Pattern pattern = Pattern.compile("<(\\w+)>(.*)</\\1>"); // 正则表达式中 \1 表示引用组 1
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            System.out.println(matcher.group(2));
        }
    }
}
