import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Getter;
import lombok.Setter;
import org.jodconverter.JodConverter;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.LocalOfficeUtils;
import org.jodconverter.office.OfficeManager;

import java.io.File;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Test {
    private Date date = new Date();

    public static void main(String[] args) throws Exception {
        System.out.println(LocalDateTime.of(2017, 1, 1, 0, 0).toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli());
        System.out.println(new Date(1483228800000L));
    }
}
