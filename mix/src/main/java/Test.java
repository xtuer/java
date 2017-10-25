import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Getter;
import lombok.Setter;
import org.jodconverter.JodConverter;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.LocalOfficeUtils;
import org.jodconverter.office.OfficeManager;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Test {
    private Date date = new Date();

    public static void main(String[] args) throws Exception {
//        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd";
        System.out.println(JSON.toJSONString(new Test(), SerializerFeature.WriteDateUseDateFormat));
    }
}
