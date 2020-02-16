public class Test {
    public static void main(String[] args) throws Exception {
        System.out.println(toCn(0));
        System.out.println(toCn(3));
        System.out.println(toCn(10));
        System.out.println(toCn(18));
        System.out.println(toCn(22));
        System.out.println(toCn(123));
        System.out.println(toCn(120));
        System.out.println(toCn(102));
        System.out.println(toCn(1000));
        System.out.println(toCn(1001));
        System.out.println(toCn(1111));
        System.out.println(toCn(1101));
        System.out.println(toCn(320_5123_2018_0912_3460L));
    }

    private static final String[] UNITS = { "万", "亿", "兆", "京", "垓", "秭" };
    public static String toCn(long n) {
        int u = 0;
        String cn = "";

        while (n > 0) {
            cn = toCnBeforeWan((int)(n%10000)) + cn; // 从后往前每 4 位分割处理
            n /= 10000;

            if (n > 0) {
                cn = UNITS[u++] + cn;
            }
        }

        return cn;
    }

    private static final String[] CN_DIGITS = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
    private static String toCnBeforeWan(int n) {
        if (n == 0) {
            return "零";
        }

        String result = CN_DIGITS[n/1000] + "千" + CN_DIGITS[(n%1000/100)] + "百" + CN_DIGITS[(n%100/10)] + "十" + CN_DIGITS[(n%10)];
        result = result.replaceAll("零千|零百|零十", "零");
        result = result.replaceAll("零+", "零");
        result = result.replaceAll("^零|零$", "");
        result = result.replaceAll("^一十", "十");

        return result;
    }
}
