import org.jodconverter.JodConverter;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.LocalOfficeUtils;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.OfficeManager;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {
        OfficeManager officeManager = LocalOfficeManager.install();

        try {
            // Start an office process and connect to the started instance (on port 2002).
            officeManager.start();
            JodConverter.convert(new File("/Users/Biao/Desktop/x/x.doc"))
                    .to(new File("/Users/Biao/Desktop/x/x.html")).execute();
        } finally {
            // Stop the office process
            LocalOfficeUtils.stopQuietly(officeManager);
        }
    }
}
