import com.mzlion.core.io.IOUtils;

import java.io.*;

public class Test {
    public static void main(String[] args) throws Exception {
        InputStream in = new FileInputStream(new File("/Users/Biao/Desktop/foo.txt"));
        OutputStream os = new FileOutputStream(new File("/Users/Biao/Desktop/y/bar.txt"));

        IOUtils.copy(in, os);

        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(os);
    }
}
