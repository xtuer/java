import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class FileUtilsTest {
    public static void main(String[] args) throws IOException {
        String path = "/Users/Biao/Desktop/foo.txt";
        System.out.println(FilenameUtils.getExtension(path)); // 后缀
        System.out.println(FilenameUtils.getBaseName(path));  // 文件名去掉后缀
        System.out.println(FilenameUtils.getFullPath(path));  // 去掉文件名的路径部分

        FileUtils.forceMkdir(new File("/Users/Biao/Desktop/a/b/c")); // 递归创建目录
    }
}
