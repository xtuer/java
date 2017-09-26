import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class Test {
    private static Logger logger = LoggerFactory.getLogger(Test.class);
    private int id;

    public static void main(String[] args) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("你好 {}", 1234);
        }

        foo();
    }

    public static void foo(String... strs) {
        System.out.println(strs);
        for (String str : strs) {
            System.out.println(strs);
        }
    }
}
