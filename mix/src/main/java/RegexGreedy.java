import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexGreedy {
    public static void main(String[] args) {
        String str = "xx<biao><>c<b>";
        Pattern pattern;
        Matcher matcher;

        // 贪婪: 最长匹配 .* : 输出: <biao><>c<b>
        pattern = Pattern.compile("<.*>");
        matcher = pattern.matcher(str);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }

        // 不知是否非贪婪 .*? : 输出: <biao>, <>, <b>
        pattern = Pattern.compile("<.*?>");
        matcher = pattern.matcher(str);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }

        // 使用组, 输出<>里的内容, 输出: 'biao', ' ', 'b'
        // 0组代表整个表达式, 子组从1开始
        pattern = Pattern.compile("<(.*?)>");
        matcher = pattern.matcher(str);
        while (matcher.find()) {
            System.out.println(matcher.group(1));
        }
    }
}
