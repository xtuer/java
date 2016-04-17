import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class CsvReader {
    public static void main(String[] args) throws Exception {
        InputStream in = CsvReader.class.getClassLoader().getResourceAsStream("test.csv");
        Reader reader = new InputStreamReader(in);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);

        for (CSVRecord record : records) {
            System.out.printf("[x: %s, y: %s]\n", record.get("x"), record.get("y"));
        }
    }
}
