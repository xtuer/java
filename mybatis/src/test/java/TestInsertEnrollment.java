import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtuer.bean.Enrollment;
import com.xtuer.editor.EnrollmentEditor;
import com.xtuer.service.EnrollmentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:config/spring-beans.xml"})
public class TestInsertEnrollment {
    @Autowired
    private EnrollmentService enrollmentService;

    @Test
    public void insertEnrollment() throws Exception {
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

    @Test
    public void insertEnrollmentsWithTransaction() throws Exception {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("enrollments.txt");
        Scanner scanner = new Scanner(in);
        List<Enrollment> enrollments = new LinkedList<Enrollment>();
        final int MAX_COUNT = 500; // 批量插入的个数
        int count = 0;

        while (scanner.hasNextLine()) {
            EnrollmentEditor editor = new EnrollmentEditor();
            editor.setAsText(scanner.nextLine().trim());
            Enrollment enrollment = (Enrollment) editor.getValue();

            if (enrollment == null) {
                continue;
            }

            enrollments.add(enrollment);
            count = (count + 1) % MAX_COUNT;

            if (count == 0) {
                enrollmentService.insertEnrollmentsWithTransaction(enrollments);
                enrollments.clear();
            }
        }

        if (enrollments.size() > 0) {
            enrollmentService.insertEnrollmentsWithTransaction(enrollments);
        }
    }
}
