import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class Test {
    private static SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
    private String name;
    private static Pattern fileUrlPattern  = Pattern.compile("/data/(file|image)/(\\d{4}-\\d{2}-\\d{2})/(\\w+\\.\\w+)\\?*.*");

    public static void main(String[] args) {
        match("/data/file/2018-04-10/168242114298118144.doc?token=");
        match("/data/file/2018-04-10/168242114298118144.doc");
        match("/data/image/2018-04-10/165694386577866752.jpg");

    }

    public static void match(String url) {
        Matcher fileMatcher = fileUrlPattern.matcher(url);

        String date = null;
        String filename = null;

        if (fileMatcher.matches()) {
            date = fileMatcher.group(2);
            filename = fileMatcher.group(3);
        }

        System.out.println(date + "/" + filename);
    }
}
