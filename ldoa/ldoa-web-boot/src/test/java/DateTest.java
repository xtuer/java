import com.xtuer.util.Utils;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class DateTest {
    @Test
    public void testDayStart() {
        System.out.println(Utils.dayStart(new Date()));
    }

    @Test
    public void testDayEnd() {
        System.out.println(Utils.dayEnd(new Date()));
    }

    @Test
    public void testYearStart() {
        System.out.println(Utils.yearStart(new Date()));
    }

    @Test
    public void testYearEnd() {
        System.out.println(Utils.yearEnd(new Date()));
    }

    @Test
    public void testMonthStart() {
        System.out.println(Utils.monthStart(new Date()));
    }

    @Test
    public void testMonthEnd() {
        System.out.println(Utils.monthEnd(new Date()));
    }
}

