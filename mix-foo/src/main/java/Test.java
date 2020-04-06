import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Test {
    public static void main(String[] args) throws IOException {
        Date zeroClock = today0Clock();

        System.out.println(zeroClock);
        System.out.println(today24Clock());
    }

    /**
     * 今日凌晨 0 点
     */
    public static Date today0Clock() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }

    public static Date today24Clock() {
        return new Date(today0Clock().getTime() + 3600*24*1000-1000);
    }
}
