import com.xtuer.bean.Enrollment;
import com.xtuer.service.EnrollmentService;
import com.xtuer.util.DbUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcInsertEnrollmentTest {
    private static final int MAX_COUNT = 500; // 批量插入的个数

    private EnrollmentService enrollmentService = new EnrollmentService();

    // 不使用事务: 插入 66720 个，使用了 11827 毫秒，11 秒
    @Test
    public void insert() throws Exception {
        new Executor(enrollmentService).execute((enrollments) -> {
            Connection conn = DbUtils.getConnection();

            for (Enrollment enrollment : enrollments) {
                insertEnrollment(enrollment, conn);
            }

            DbUtils.close(null, null, conn);
        });
    }

    // 使用事务: 插入 66720 个，使用了 5256 毫秒，5 秒
    @Test
    public void insertEnrollmentWithTransaction() throws Exception {
        new Executor(enrollmentService).execute((enrollments) -> {
            int length = enrollments.size();
            Connection conn = DbUtils.getConnection();

            conn.setAutoCommit(false);
            for (int i = 0; i < length; i++) {
                insertEnrollment(enrollments.get(i), conn);

                if (i > 0 && i % MAX_COUNT == 0) {
                    conn.commit();
                }
            }
            conn.commit();

            DbUtils.close(null, null, conn);
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

        DbUtils.close(null, stmt, null);
    }
}
