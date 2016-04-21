public class Test {
    public static long stringToNumber(String str) {
        String[] components = str.split("\\.");
        long result = 0;

        for (int i = 0; i < components.length; ++i) {
            result = result * 100 + Integer.parseInt(components[i]);
        }

        return result;
    }

    public static void main(String[] args) {
        long r1 = stringToNumber("3.4.1");
        long r2 = stringToNumber("3.4");
        System.out.printf("r1: %d, r2: %d, r1 > r2: %b", r1, r2, r1 > r2);
    }
}
