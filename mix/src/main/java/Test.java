import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) throws Exception {
        String str = "<biao>";
        Pattern pattern;
        Matcher matcher;

        // 贪婪: 最长匹配 .* : 输出: <biao><>c<b>
        pattern = Pattern.compile("<.*>");
        matcher = pattern.matcher(str);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }

        // 不知是否非贪婪 .*? : 输出: <biao>, <>, <b>
        pattern = Pattern.compile("<.*+>");
        matcher = pattern.matcher(str);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
