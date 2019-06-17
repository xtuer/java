public class Test {
    public static void main(String[] args) throws Exception {
        double r = 100;
        int n = 16;
        double x = aroundCircleRadius(r, n);

        System.out.println(x);
        System.out.println(2*Math.PI*(r+x));
        System.out.println(2*n*x);
    }

    /**
     * 计算紧紧围绕半径为 r 的大圆的 n 个小圆的半径 (大圆和小圆相切，小圆之间相切)
     *
     * @param r 大圆的半径
     * @param n 小圆的个数
     * @return 返回小圆的半径
     */
    public static double aroundCircleRadius(double r, int n) {
        // 圆的内切正 n 边型的边长为 2*r*sin(π/n)
        // 小圆的半径为 x, 计算半径为 r+x 的圆的 2n 个内切正多边形的边长 b, b 等于 x
        double t = Math.sin(Math.PI/2/n);
        return (2*r*t) / (1-2*t);
    }
}
