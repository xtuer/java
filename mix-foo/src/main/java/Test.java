import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 人脸检测与属性分析
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(LocalDateTime.of(2300, 1, 1, 0, 0, 0).toEpochSecond(ZoneOffset.UTC));
        System.out.println(LocalDateTime.of(2300, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli());
        System.out.println(System.currentTimeMillis());
    }
}
