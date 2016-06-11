import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.SystemUtils;

import java.util.Date;

public class Test {
    public static void main(String[] args) {
        System.out.println(StringEscapeUtils.escapeHtml4("<script>echo() 黄彪</script>"));
    }
}
