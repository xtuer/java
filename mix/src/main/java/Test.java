import java.io.File;

public class Test {
    public static void main(String[] args) throws Exception {
        File dir = new File("/Users/Biao/Documents/套卷/高中数学（2183套）/GSZH030C");
        int count = 0;

        for (File file : dir.listFiles()) {
            System.out.println(file.getName());
            ++count;
        }

        System.out.println(count);
    }
}
