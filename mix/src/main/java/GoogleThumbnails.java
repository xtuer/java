import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.coobird.thumbnailator.Thumbnails;

@Getter
@Setter
@Accessors(chain = true)
public class GoogleThumbnails {
    public static void main(String[] args) throws Exception {
        Thumbnails.of("/Users/Biao/Pictures/bridge.jpg")
                .size(300, 300).outputQuality(0.7)
                .toFile("/Users/Biao/Desktop/x.jpg");
    }
}
