import org.apache.commons.lang3.StringUtils;

public class CommentGenerator {
    public static void main(String[] args) {
        String[] texts = {"XJson"};
        System.out.println(generateComment(texts));
    }

    public static String generateComment(String ... texts) {
        int len = 120;
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
}
