import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Test {
    public static void main(String[] args) throws IOException {
        System.out.println(sum(4, 3, 1));
    }

    /**
     *
     * @param w
     * @param h
     * @param refW
     * @param refH
     * @return
     */
    public static Dimension scaleKeepAspectRatio(int w, int h, int refW, int refH) {
        // 1. 计算宽和高的比例
        // 2. 如果宽或高任意一个大于对应的参考宽或高, 则需要进行缩放
        // 3. 使用等比缩放, 取宽高比例中最大的比例作为缩放比例

        int resultW   = w;
        int resultH   = h;
        double ratioW = ((double) w) / refW; // 宽的比例
        double ratioH = ((double) h) / refH; // 高的比例
        double ratio  = ratioW > ratioH ? ratioW : ratioH; // 缩放比例

        if (w > refW || h > refH) {
            resultW = (int) (w / ratio);
            resultH = (int) (h / ratio);
        }

        return new Dimension(resultW, resultH);
    }

    public static int pow(int x, int n, int t) {
        if (n < 1) {
            return t;
        } else {
            return pow(x, n-1, t*x);
        }
    }

    public static int sum(int... items) {
        int total = 0;

        for (int item : items) {
            total += item;
        }

        return total;
    }
}

