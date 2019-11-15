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
        Exam exam = new Exam();
        Date startTime = new Date();
        Date endTime = new Date(startTime.getTime() + 60*60*1000);
        exam.setId(1).setPaperId(1).setClazzId(1).setTitle("测试考试").setMaxTimes(1)
                .setDuration(60*60).setStartTime(startTime).setEndTime(endTime);
        examMapper.upsertExam(exam);
    }

    /**
     * 测试创建考试记录
     */
    @Test
    public void createExamRecord() {
        Utils.dump(examService.insertExamRecord(1, 1));
    }

    /**
     * 查询考试记录
     */
    @Test
    public void findExamRecord() {
        Utils.dump(examService.findExamRecord(1, 2));
    }
}
