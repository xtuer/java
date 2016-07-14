import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtuer.bean.Enrollment;
import com.xtuer.editor.EnrollmentEditor;
import com.xtuer.service.EnrollmentService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Foo {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:config/spring-beans.xml");
        EnrollmentService enrollmentService = context.getBean(EnrollmentService.class);

        String[] enrollmentStrings = {
                "63300011 张爱芳 100101198910240012 00208 国际财务管理 101102 北京市通州区 9090 北京大学 31 04 8888 2016-06-22 00:00:00 2016-06-22 18:30:00 是 9090 北京大学",
                "63300012 张爱芳 100101198910240012 00208 国际财务管理 101102 北京市通州区 9090 北京大学 31 04 8888 2016-06-22 00:00:00 2016-06-22 18:30:00 是 9090 北京大学",
                "63300013 张爱芳 100101198910240012 00208 国际财务管理 101102 北京市通州区 9090 北京大学 31 04 8888 2016-06-22 00:00:00 2016-06-22 18:30:00 是 9090 北京大学",
                "63300013 张爱芳 100101198910240012 00208 国际财务管理 101102"
        };

        ObjectMapper mapper = new ObjectMapper();

        for (String enrollmentString : enrollmentStrings) {
            EnrollmentEditor editor = new EnrollmentEditor();
            editor.setAsText(enrollmentString);
            Enrollment enrollment = (Enrollment) editor.getValue();

            if (enrollment != null) {
                enrollmentService.insertEnrollmentWithTransaction(enrollment);
            }
        }
    }
}
