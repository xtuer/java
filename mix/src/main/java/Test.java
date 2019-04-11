import lombok.Builder;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Builder
@ToString
@Slf4j
public class Test {
    String name;
    int age;

    public static void main(String[] args) {
        Test test = Test.builder().age(23).name("Biao").build();
        System.out.println(test);

        int[] ns = {1, 2, 3, 4, 5, 6};
        System.out.println(binarySearch(ns, 0));
        System.out.println(binarySearch(ns, 1));
        System.out.println(binarySearch(ns, 3));
        System.out.println(binarySearch(ns, 6));
        System.out.println(binarySearch(ns, 8));
        log.info("Hello {}", "Biao");
    }

    public static int binarySearch(int[] ns, int n) {
        int l = 0;
        int h = ns.length-1;

        while (l <= h) {
            int m = (l + h) / 2;

            if (n < ns[m]) {
                h = m - 1;
            } else if (n > ns[m]) {
                l = m + 1;
            } else if (n == ns[m]) {
                return m;
            }
        }

        return -1;
    }
}
