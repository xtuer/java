import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class Test {

    /**
     * 获取图片的大小
     *
     * This solution is very quick as only image size is read from the file and not the whole image.
     * From: https://stackoverflow.com/questions/672916/how-to-get-image-height-and-width-using-java
     *
     * Blows ImageIO.read() completely out of the water, both in terms of CPU time and memory usage.
     *
     * @param path 图片文件的路径
     * @return 返回图片大小的 dimension 对象
     */
    public static Dimension getImageSize(String path) {
        Dimension result = null;
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix("png");

        if (iter.hasNext()) {
            ImageReader reader = iter.next();
            try {
                ImageInputStream stream = new FileImageInputStream(new File(path));
                reader.setInput(stream);
                int width  = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                result = new Dimension(width, height);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                reader.dispose();
            }
        } else {
            System.out.println("No ImageReader found for given format: png");
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(getImageSize("./x.png"));
    }
}
