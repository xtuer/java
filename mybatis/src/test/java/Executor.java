import com.xtuer.bean.Enrollment;
import com.xtuer.service.EnrollmentService;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * 使用策略模式简化代码
 */
public class Executor {
    private static final String FILE_NAME = "enrollments.txt";

    public void execute(EnrollmentService enrollmentService, Executable executable) throws SQLException {
        // 读取数据，计时都放到模版里
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(FILE_NAME);
        List<Enrollment> enrollments = enrollmentService.readEnrollments(in);

        long start = System.currentTimeMillis(); // 开始时间
        executable.execute(enrollments); // [*] 执行操作
        long end = System.currentTimeMillis(); // 结束时间

        long time = end - start; // 消耗时间
        System.out.printf("插入 %d 个，使用了 %d 毫秒，%d 秒\n", enrollments.size(), time, time / 1000);
    }
}

interface Executable {
    void execute(List<Enrollment> enrollments) throws SQLException;
}
