import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

public class ExamImport {
    public static void main(String[] args) throws Exception {
        InputStream in = new FileInputStream("/Users/Biao/Desktop/20191017.csv");

        try (Reader reader = new InputStreamReader(Objects.requireNonNull(in))) {
            Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);

            long start = System.currentTimeMillis();
            for (CSVRecord record : records) {
                record.get("考籍号");
                // System.out.printf("%s\n", record.get("考籍号"));
            }
            long end = System.currentTimeMillis();
            System.out.println((end - start) + "ms");
        }
    }
}
