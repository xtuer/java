import com.alibaba.fastjson.JSON;
import com.xtuer.bean.User;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:spring-beans.xml"})
public class TestOxm {
    @Autowired
    private CastorMarshaller marshaller;

    private static final String FILE_NAME = "/Users/Biao/Desktop/user.xml";

    /**
     * 把 User 的对象 marshalling 生成 xml 文件 user.xml
     */
    @Test
    public void testMarshalling() throws Exception {
        User user = new User(12, "Alice", "Passw0rd");
        FileOutputStream fos = FileUtils.openOutputStream(new File(FILE_NAME));
        marshaller.marshal(user, new StreamResult(fos));
        IOUtils.closeQuietly(fos);
    }

    /**
     * 从 user.xml 文件 unmarshalling 生成 User 的对象
     */
    @Test
    public void testUnmarshalling() throws Exception {
        FileInputStream fis = FileUtils.openInputStream(new File(FILE_NAME));
        User user = (User)marshaller.unmarshal(new StreamSource(fis));
        System.out.println(JSON.toJSONString(user));
        IOUtils.closeQuietly(fis);
    }
}
