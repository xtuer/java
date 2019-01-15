import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        System.out.println(NumberUtils.toLong("abc", 12));
    }
}

