import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        testRand();
    }

    public static void testRand() {
        Random rand = new Random(103);

        for (int i = 0; i < 100; ++i) {
            System.out.println(i + ": " + rand.nextInt() % 100);
        }
    }

    public static void testFrequent() {
        Random rand = new Random("102");
        Map<Integer, Integer> map = new HashMap<>();
        int max = 10;

        for (int i = 0; i < max; ++i) {
            map.put(i, 0);
        }

        for (int i = 0; i < 1000; ++i) {
            int r = rand.nextInt() % max;
            map.put(r, map.get(r) + 1);
        }

        System.out.println(map);
    }
}

class Random {
    private long seed;

    public Random(long seed) {
        this(seed + "");
    }

    public Random(String seed) {
        this.seed = seed.hashCode();
    }

    public int nextInt() {
        seed = (seed * 9301 + 49297) % 233280;
        double t = this.seed / 233280.0;

        return (int) Math.abs(Math.ceil(t * 10000000));
    }
}
