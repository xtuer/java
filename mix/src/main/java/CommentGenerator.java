import org.apache.commons.lang3.StringUtils;

public class CommentGenerator {
    public static void main(String[] args) {
        String[] texts = {"Main entry"};
        System.out.println(generateComment1(texts));
        System.out.println(generateComment2(texts));
    }

    public static String generateComment1(String ... texts) {
        int len = 80;
        StringBuilder sb = new StringBuilder();

        // 开头
        sb.append("/").append(StringUtils.repeat("*", len - 1)).append("\n");

        // 内容
        for (String text : texts) {
            sb.append(" *").append(StringUtils.center(text, len - 3)).append("*\n");
        }

        // 结尾
        sb.append(" ").append(StringUtils.repeat("*", len - 2)).append("/");

        return sb.toString();
    }

    public static String generateComment2(String ... texts) {
        int len = 80;
        StringBuilder sb = new StringBuilder();

        // 开头
        sb.append("/*").append(StringUtils.repeat("-", len - 3)).append("|").append("\n");

        // 内容
        for (String text : texts) {
            sb.append(" |").append(StringUtils.center(text, len - 3)).append("|\n");
        }

        // 结尾
        sb.append(" |").append(StringUtils.repeat("-", len - 4)).append("*/");

        return sb.toString();
    }
}
