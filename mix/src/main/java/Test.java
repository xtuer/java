import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Test {
    String name;
    int age;

    public static void main(String[] args) {
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
        int h = ns.length;

        while (l < h) {
            int m = (l + h) / 2;

            if (n < ns[m]) {
                h = m;
            } else if (n > ns[m]) {
                l = m + 1;
            } else if (n == ns[m]) {
                return m;
            }
        }

        return -1;
    }
}
