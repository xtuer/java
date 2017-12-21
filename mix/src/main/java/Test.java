import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class Test {
    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 100000; ++i) {
            FileUtils.writeStringToFile(new File("/Users/Biao/Desktop/xyz/" + i + ".json"), "" + i, "UTF-8");
            System.out.println(i);
        }
    }
}
