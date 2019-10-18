import com.csvreader.CsvReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CsvReaderDemo {
    public static void main(String[] args) throws Exception {
        apacheCsvReader();
        javaCsvReader();
    }

    private static void apacheCsvReader() {
        InputStream in = CsvReaderDemo.class.getClassLoader().getResourceAsStream("test.csv");

        try (Reader reader = new InputStreamReader(Objects.requireNonNull(in))) {
            Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);

            for (CSVRecord record : records) {
                System.out.printf("[x: %s, y: %s]\n", record.get("x"), record.get("y"));
            }
        } catch (IOException ignore) {}
    }

    private static void javaCsvReader() throws IOException {
        CsvReader csvReader = new CsvReader("/Users/Biao/Documents/workspace/Java/mix/src/main/resources/test.csv", ',', StandardCharsets.UTF_8);
        csvReader.readHeaders();

        while (csvReader.readRecord()) {
            System.out.printf("[x: %s, y: %s]\n", csvReader.get("x"), csvReader.get("y"));
        }
    }
}
