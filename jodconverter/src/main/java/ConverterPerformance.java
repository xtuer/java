import org.jodconverter.DocumentConverter;
import org.jodconverter.office.OfficeException;
import org.jodconverter.spring.JodConverterBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.File;

public class ConverterPerformance {
    public static void main(String[] args) throws OfficeException {
        // 程序启动的时候创建 converterBean 对象，然后程序中使用它的 converter 进行转换
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        JodConverterBean converterBean = context.getBean("converterBean", JodConverterBean.class);
        long start = 0;

        // DocumentConverter 支持把 doc, docx, xls 等 office 文档转换为 pdf, html
        // 把 doc 转换为 pdf，目标格式根据输出文件名的后缀自动决定
        // 启动 LibreOffice 到连接上 LibreOffice 大概需要 30s，第一次转换约 8s，以后转换就很快了
        DocumentConverter converter = converterBean.getConverter();

        // 转为 pdf: 8s
        start = System.currentTimeMillis();
        converter.convert(new File("/Users/Biao/Desktop/使用说明.doc")).to(new File("/Users/Biao/Desktop/out/使用说明-1.pdf")).execute();
        System.out.println("需要 " + ((System.currentTimeMillis() - start) / 1000) + "秒");

        // 转为 html: 1s
        start = System.currentTimeMillis();
        converter.convert(new File("/Users/Biao/Desktop/使用说明.doc")).to(new File("/Users/Biao/Desktop/out/使用说明-1.html")).execute();
        System.out.println("需要 " + ((System.currentTimeMillis() - start) / 1000) + "秒");

        // 转为 pdf: 2s
        start = System.currentTimeMillis();
        converter.convert(new File("/Users/Biao/Desktop/使用说明.doc")).to(new File("/Users/Biao/Desktop/out/使用说明-2.pdf")).execute();
        System.out.println("需要 " + ((System.currentTimeMillis() - start) / 1000) + "秒");

        // 转为 html: 1s
        start = System.currentTimeMillis();
        converter.convert(new File("/Users/Biao/Desktop/使用说明.doc")).to(new File("/Users/Biao/Desktop/out/使用说明-2.html")).execute();
        System.out.println("需要 " + ((System.currentTimeMillis() - start) / 1000) + "秒");
    }
}
