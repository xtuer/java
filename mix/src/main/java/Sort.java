public class Sort {
    // 插入排序，假设 n-1 个元素是排好序的
    public static void insertSort(int[] a) {
        for (int i = 1; i < a.length; ++i) {
            for (int t = a[i], j = i; j > 0; --j) {
                if (t < a[j-1]) {
                    a[j] = a[j-1];
                } else {
                    a[j] = t;
                    break;
                }
            }
        }
    }

    // 合并 2 个有序数组
    public static int[] mergeSortedArrays(int[] a1, int[] a2) {
        int[] r = new int[a1.length + a2.length];
        int c = 0, i = 0, j = 0;

        while (i < a1.length && j < a2.length) {
            if (a1[i] < a2[j]) {
                r[c++] = a1[i++];
            } else {
                r[c++] = a2[j++];
            }
        }

        while (i < a1.length) {
            r[c++] = a1[i++];
        }

        while (j < a2.length) {
            r[c++] = a2[j++];
        }

        return r;
    }
}
