import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

public class VelocityTest {
    @Test
    public void generate() {
        // [1] 配置 Velocity
        String templateDirectory = "/Users/Biao/Documents/workspace/Java/web-mix/src/main/webapp/WEB-INF/view/";
        Properties properties = new Properties();
        properties.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, templateDirectory);
        properties.setProperty(VelocityEngine.INPUT_ENCODING, "UTF-8");
        properties.setProperty(VelocityEngine.OUTPUT_ENCODING, "UTF-8");
        Velocity.init(properties);

        // [2] 读取模版文件
        Template template = Velocity.getTemplate("hello.vm");

        // [3] 准备模版使用的数据
        VelocityContext ctx = new VelocityContext();
        ctx.put("name", "Hello");
        ctx.put("static", "static path");

        // [4] 使用模版和数据生成静态内容，可以用来实现页面静态化，放在 Redis 或则 Nginx 下
        Writer writer = new StringWriter();
        template.merge(ctx, writer);

        System.out.println(writer);

    }
}
