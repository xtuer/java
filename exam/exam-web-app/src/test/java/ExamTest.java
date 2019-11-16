import com.exam.bean.exam.Exam;
import com.exam.mapper.exam.ExamMapper;
import com.exam.service.exam.ExamService;
import com.exam.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:config/application.xml"})
public class ExamTest {
    @Autowired
    private ExamService examService;

    @Autowired
    private ExamMapper examMapper;

    /**
     * 创建考试
     */
    @Test
    public void upsertExam() {
        // 1. 创建一份试卷，试卷 ID 修改为 1 (手动)
        // 2. 创建考试，考试 ID、试卷 ID、班级 ID 都为 1 (下面)
        Date startTime = new Date();
        Date endTime = new Date(startTime.getTime() + 60*60*1000);

        Exam exam = new Exam();
        exam.setId(1).setPaperId(1).setHolderId(1).setTitle("测试考试").setMaxTimes(1)
                .setDuration(60*60).setStartTime(startTime).setEndTime(endTime);
        Utils.dump(examService.upsertExam(exam));
    }

    /**
     * 测试创建考试记录
     */
    @Test
    public void createExamRecord() {
        // 给用户 1 的考试 1 创建考试记录
        Utils.dump(examService.insertExamRecord(1, 1));
    }

    /**
     * 查询考试记录
     */
    @Test
    public void findExamRecord() {
        // 查询用户 1 的考试 1 的考试记录
        Utils.dump(examService.findExamRecord(1, 1));
    }
}
