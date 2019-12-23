import java.util.Arrays;
import java.util.Random;

public class Test {
    public static void main(String[] args) {
        final int len = 100;
        int[] arr = buildArray(len);
        int[][] buckets = new int[10][len + 1]; // 10 个桶，每个桶最多存储 len 个数据，最后一个元素为桶中元素的个数

        int max = Integer.MIN_VALUE; // arr 中的最大值
        for (int i = 0; i < len; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }

        int loop = 0; // 最大数值的位数
        while (max > 0) {
            max /= 10;
            loop++;
        }

        int d = 1;
        for (int s = 0; s < loop; s++) {
            // 把 arr 中的元素按照个、十、百分别存储到桶中
            for (int i = 0; i < len; i++) {
                int bucketIndex = arr[i] / d % 10;
                int size = buckets[bucketIndex][len];
                buckets[bucketIndex][size] = arr[i];
                buckets[bucketIndex][len] += 1;
            }

            // 复制桶里的数据到 arr
            int t = 0;
            for (int i = 0; i < 10; i++) {
                int size = buckets[i][len];

                for (int j = 0; j < size; j++) {
                    arr[t++] = buckets[i][j];
                }

                buckets[i][len] = 0;
            }

            d *= 10;
        }

        System.out.println(Arrays.toString(arr));
    }

    public static int[] buildArray(int length) {
        int[] array = new int[length];
        Random rand= new Random();

        for (int i = 0; i < length; i++) {
            array[i] = rand.nextInt(1_000_000);
        }

        return array;
    }
}
