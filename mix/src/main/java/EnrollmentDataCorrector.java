import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class EnrollmentDataCorrector {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(new File("/Users/Biao/Desktop/201607.txt"));
        PrintWriter writer = new PrintWriter(new File("/Users/Biao/Desktop/201607-1.txt"));

        int count = 0;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            String first = line.substring(0, 35);
            String second = line.substring(35);

            first = first.replaceAll("[ ]+", "");
            line = first + second;
            line = line.replaceAll("\\s+", " ");
            writer.println(line);

            count++;
            count %= 50;

            if (count == 0) {
                writer.flush();
            }
        }

        writer.close();
    }
}
