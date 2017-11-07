import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.coobird.thumbnailator.Thumbnails;

import java.io.BufferedReader;
import java.io.FileReader;

@Getter
@Setter
@Accessors(chain = true)
public class GoogleThumbnails {
    public static void main(String[] args) throws Exception {

        Thumbnails.of("/Users/Biao/Pictures/Rock_stars_by_vladstudio2.jpg")
                .size(300, 300).outputQuality(0.7)
                .toFile("/Users/Biao/Desktop/x.jpg");
    }
}
