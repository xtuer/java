package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date stringToDate(String date) {
        try {
            return formatter.parse(date);
        } catch (ParseException ignored) {
        }

        return null;
    }

    public static void main(String[] args) {
        System.out.println(stringToDate("2019-07-13 09:00:00"));
    }
}
