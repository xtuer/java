import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;

public class CsvWriter {
    public static void main(String[] args) throws IOException {
        //Create the CSVFormat object with "\n" as a record delimiter
        StringBuilder result = new StringBuilder();
        CSVFormat csvFormat =  CSVFormat.DEFAULT.withRecordSeparator("\n");
        CSVPrinter csvPrinter = new CSVPrinter(result, csvFormat);

        csvPrinter.printRecord("ID", "username", "password");
        csvPrinter.printRecord("1", "Alice", "Passw0rd");
        csvPrinter.printRecord("2", "Bob", "123456");
        csvPrinter.printRecord("3", "John", "Aloha");
        csvPrinter.close();

        System.out.println(result);
    }
}
