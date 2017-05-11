import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
    public static void main(String[] args) throws Exception {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse("1979-12-30");
            System.out.print(new SimpleDateFormat("YYYY-MM-dd").format(date));
        } catch (Exception e) {
        }
    }
}
