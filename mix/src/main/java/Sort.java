import java.util.Arrays;
import java.util.Random;

public class Sort {
    public static void main(String[] args) {
        int[] a, b, c, d;

        System.out.println("Insert sort:");
        a = generateValues(10);
        insertSort(a);
        System.out.println(Arrays.toString(a));

        System.out.println("Quick sort:");
        a = generateValues(10);
        quickSort(a);
        System.out.println(Arrays.toString(a));

        System.out.println("Heap sort:");
        b = generateValues(10);
        heapSort(b);
        System.out.println(Arrays.toString(b));

        System.out.println("Merge Sorted Arrays:");
        c = mergeSortedArrays(a, b);
        System.out.println(Arrays.toString(c));

        System.out.println("Smallest k values:");
        c = generateValues(10);
        d = topK(c, 5);
        quickSort(c);
        System.out.println(Arrays.toString(c));
        System.out.println(Arrays.toString(d));
    }

    /**
     * Generate an array with random integer values
     *
     * @param length is the length of the array
     * @return an array with random integer values
     */
    public static int[] generateValues(int length) {
        int[] a = new int[length];
        Random rand = new Random(System.nanoTime());

        for (int i = 0; i < a.length; ++i) {
            a[i] = rand.nextInt(100);
        }

        return a;
    }

    ///////////////////////////////////////////////////////////////////
    // Directly insert sort (improved insert sort: shell sort, jump)
    ///////////////////////////////////////////////////////////////////
    public static void insertSort(int[] a) {
        for (int i = 1; i < a.length; ++i) {
            if (a[i] < a[i - 1]) {
                int temp = a[i];
                int j;
                for (j = i; j > 0 && temp < a[j - 1]; --j) {
                    a[j] = a[j - 1];
                }
                a[j] = temp;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////
    // Quick sort
    ///////////////////////////////////////////////////////////////////
    public static void quickSort(int[] a) {
        quickSort(a, 0, a.length - 1);
    }

    public static void quickSort(int[] a, int low, int high) {
        if (low < high) {
            int pivot = partition(a, low, high);

            quickSort(a, low, pivot - 1);
            quickSort(a, pivot + 1, high);
        }
    }

    public static int partition(int[] a, int low, int high) {
        int pivotValue = a[low]; // a[low] 空出来

        // 大于 pivotValue 的数都放到右边
        // 小于 pivotValue 的数都放到左边
        while (low < high) {
            // 从右向左找到小于 pivotValue 的数，放到 a[low]，此时则 a[high] 空出来
            while (low < high && a[high] >= pivotValue) {
                --high;
            }
            a[low] = a[high];

            // 从左向右找到大于 pivotValue 的数，放到 a[high]，此时则 a[low] 空出来
            while (low < high && a[low] <= pivotValue) {
                ++low;
            }
            a[high] = a[low];
        }

        a[low] = pivotValue;

        return low; // 最后肯定是 low == high
    }

    public static void swap(int[] ns, int a, int b) {
        int temp = ns[a];
        ns[a] = ns[b];
        ns[b] = temp;
    }

    ///////////////////////////////////////////////////////////////////
    // Heap sort
    ///////////////////////////////////////////////////////////////////
    public static void heapSort(int[] a) {
        // 把整个数组调整为大顶堆
        for (int i = (a.length/2)-1; i>=0; --i) {
            adjustHeap(a, i, a.length);
        }

        // a[0] 为最大的数，和最后一个交换，再把数组调整为大顶堆
        for (int i = a.length - 1; i > 0; --i) {
            swap(a, 0, i);
            adjustHeap(a, 0, i);
        }
    }

    public static void adjustHeap(int[] a, int start, final int end) {
        int temp = a[start];

        for (int i = (start << 1) + 1; i < end; i = (i << 1) + 1) { // i = i * 2
            if (i+1<end && a[i]<a[i+1]) ++i;
            if (temp >= a[i]) break;

            a[start] = a[i];
            start = i;
        }

        a[start] = temp;
    }

    // 合并 2 个有序数组
    public static int[] mergeSortedArrays(int[] a, int[] b) {
        int[] r = new int[a.length + b.length];
        int i = 0, j = 0, k = 0;

        while (i<a.length && j<b.length) {
            if (a[i] < b[j]) {
                r[k++] = a[i++];
            } else {
                r[k++] = b[j++];
            }
        }

        while (i < a.length) {
            r[k++] = a[i++];
        }

        while (j < b.length) {
            r[k++] = b[j++];
        }

        return r;
    }

    public static int[] topK(int[] a, int k) {
        if (k >= a.length) {
            return a;
        }

        int[] h = new int[k];
        int i = 0;

        // 取前 k 个元素
        for (; i < k; ++i) {
            h[i] = a[i];
        }

        // 把前 k 个元素调整为大顶堆
        for (int j = h.length / 2; j >= 0; --j) {
            adjustHeap(h, j, h.length);
        }

        // 如果小于堆顶，替换掉堆顶，调整为大顶堆
        for (; i < a.length; ++i) {
            if (a[i] < h[0]) {
                h[0] = a[i];
                adjustHeap(h, 0, h.length);
            }
        }

        return h;
    }
}
