import java.util.Date;

public class Test {
    public static void main(String[] args) {
        Date date = new Date();
        date.setTime(1483200000000L);
        System.out.println(date);
        System.out.println(new Date().getTime());
    }
}
