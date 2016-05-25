import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Test {
    public static void main(String[] args) throws Exception {
        System.out.println(StringUtils.capitalize("hello world"));
        System.out.println(StringUtils.join("1", "2"));

        String[] tokens = {"1", "2"};
        System.out.println(StringUtils.join(tokens, ','));

        // Checks if a CharSequence is whitespace, empty ("") or null
        System.out.println(StringUtils.isBlank("    ")); // true
    }
}
