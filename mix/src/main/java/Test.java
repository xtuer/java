import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class Test {
    public static final Pattern REPO_FILE_URL_PATTERN = Pattern.compile("/file/repo/([\\d-]+)/(.+)");

    public static void foo(String... args) {
        System.out.println(String.join("/", args));
    }

    public static void main(String[] args) throws Exception {
        System.out.println("/file/download/sbc".substring(15));
    }
}
