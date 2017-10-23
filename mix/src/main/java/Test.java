import org.jodconverter.JodConverter;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.LocalOfficeUtils;
import org.jodconverter.office.OfficeManager;

import java.io.File;

public class Test {
    public static void main(String[] args) throws Exception {
        OfficeManager officeManager = LocalOfficeManager.install();

        try {
            // Start an office process and connect to the started instance (on port 2002).
            officeManager.start();
            JodConverter.convert(new File("/Users/Biao/Desktop/使用说明.doc")).to(new File("/Users/Biao/Desktop/out/使用说明.html")).execute();
        } finally {
            // Stop the office process
            LocalOfficeUtils.stopQuietly(officeManager);
        }
    }
}
