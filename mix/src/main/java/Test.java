import org.apache.commons.lang3.exception.ExceptionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
    public static void main(String[] args) throws ParseException {
        char sn = 65;
        String mark = "" + (sn++);
        System.out.println(mark);
    }
}
