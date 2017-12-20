import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;

public class Test {
    public static void main(String[] args) {
        Collection<File> found = FileUtils.listFiles(new File("/Users/Biao/Documents/套卷/知识点"), new String[]{"csv"}, true);
        found.stream().forEach(System.out::println);
    }
}
