import java.util.Random;

public class Test {
    public static void main(String[] args) {
//        for (int i = 1; i < 29; ++i) {
//            if (i < 10) {
//                System.out.print("'2016-02-0" + i + "', ");
//            } else {
//                System.out.print("'2016-02-" + i + "', ");
//            }
//        }

        Random random = new Random();
        for (int i = 1; i < 10; ++i) {
            System.out.print(random.nextInt(200) + ", ");
        }

//        for (int i = 1; i < 17; ++i) {
//            System.out.print("'高三 " + i + " 班', ");
//        }
    }
}
