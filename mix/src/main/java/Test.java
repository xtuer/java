import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Test {
    private static Logger logger = LoggerFactory.getLogger(Test.class);
    private int id;

    public static void main(String[] args) throws Exception {
        System.out.println(Instant.now().getEpochSecond());
        System.out.println(System.currentTimeMillis() / 1000);
    }
}
