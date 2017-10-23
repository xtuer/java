import org.jodconverter.JodConverter;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.LocalOfficeUtils;
import org.jodconverter.office.OfficeManager;

import java.io.File;

public class ConverterInLocal {
    public static void main(String[] args) throws Exception {
        OfficeManager officeManager = LocalOfficeManager.install();

        try {
            officeManager.start();

            File src = new File("/Users/Biao/Desktop/使用说明.doc");
            File dst = new File("/Users/Biao/Desktop/out/使用说明.html");
            JodConverter.convert(src).to(dst).execute();
        } finally {
            LocalOfficeUtils.stopQuietly(officeManager);
        }
    }
}
