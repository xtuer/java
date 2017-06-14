import com.xtuer.bean.Enrollment;
import com.xtuer.service.EnrollmentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:config/spring-beans.xml"})
public class JdbcInsertEnrollmentTest {
    @Resource(name="dataSource")
    private DataSource dataSource;

    private static final int MAX_COUNT = 500; // 批量插入的个数

    private EnrollmentService enrollmentService = new EnrollmentService();

    // 不使用事务: 插入 66720 个，使用了 11827 毫秒，11 秒
    @Test
    public void insert() throws Exception {
        new Executor(enrollmentService).execute((enrollments) -> {
            Connection conn = dataSource.getConnection();

            for (Enrollment enrollment : enrollments) {
                insertEnrollment(enrollment, conn);
            }

            close(null, null, conn);
        });
    }

    // 使用事务: 插入 66720 个，使用了 5256 毫秒，5 秒
    // 插入 20000 个，使用了 614386 毫秒，614 秒
    @Test
    public void insertEnrollmentWithTransaction() throws Exception {
        new Executor(enrollmentService).execute((enrollments) -> {
            int length = enrollments.size();
            Connection conn = dataSource.getConnection();

            conn.setAutoCommit(false);
            for (int i = 0; i < length; i++) {
                insertEnrollment(enrollments.get(i), conn);

                if (i > 0 && i % MAX_COUNT == 0) {
                    conn.commit();
                }
            }
            conn.commit();

            close(null, null, conn);
        });
    }

    // 插入 Enrollment
    private void insertEnrollment(Enrollment enrollment, Connection conn) throws SQLException {
        String sql = "INSERT INTO enrollment "
                + "(exam_uid, subject_code, site_code, room_code, seat_code, period_unit_code) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, enrollment.getExamUId());
        stmt.setString(2, enrollment.getSubjectCode());
        stmt.setString(3, enrollment.getSiteCode());
        stmt.setString(4, enrollment.getRoomCode());
        stmt.setString(5, enrollment.getSeatCode());
        stmt.setString(6, enrollment.getPeriodUnitCode());

        stmt.execute();

        close(null, stmt, null);
    }

    /**
     * 释放数据库资源
     */
    private void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
