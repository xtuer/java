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
public class MyBatisInsertEnrollmentTest {
    private static final int MAX_COUNT = 1000; // 批量插入的个数

    @Autowired
    private EnrollmentService enrollmentService;

    // 不标记为事务: 插入 66720 个，使用了 12872 毫秒，12 秒
    @Test
    public void insert() throws Exception {
        new Executor(enrollmentService).execute((enrollments) -> {
            for (Enrollment enrollment : enrollments) {
                enrollmentService.insertEnrollment(enrollment);
            }
        });
    }

    // 使用事务单行插入: 插入 66720 个，使用了 23286 毫秒，23 秒
    @Test
    public void insertWithTransaction() throws Exception {
        new Executor(enrollmentService).execute((enrollments) -> {
            for (Enrollment enrollment : enrollments) {
                enrollmentService.insertEnrollmentWithTransaction(enrollment);
            }
        });
    }

    // 使用事务多行插入: 插入 66720 个，使用了 6853 毫秒，6 秒
    @Test
    public void insertEnrollmentsWithTransaction() throws Exception {
        new Executor(enrollmentService).execute((enrollments) -> {
            int length = enrollments.size();

            for (int i = 0, end = 0; i < length; i = end) {
                end = Math.min(i + MAX_COUNT, length);

                List<Enrollment> ens = enrollments.subList(i, end); // MAX_COUNT 个作为一个事务插入
                enrollmentService.insertEnrollmentsWithTransaction(ens);
            }
        });
    }
}
