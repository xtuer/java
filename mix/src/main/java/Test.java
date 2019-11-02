import org.apache.commons.lang3.exception.ExceptionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
    public static void main(String[] args) throws ParseException {
        String date = "2019-11-14T16:00:00.800";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        ThreadLocal<SimpleDateFormat> formatter = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        System.out.println(formatter.get().parse(date));
        System.out.println(formatter.get().format(new Date()));
    }
}
