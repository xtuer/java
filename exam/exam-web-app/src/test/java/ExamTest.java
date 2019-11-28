import com.exam.bean.exam.Exam;
import com.exam.bean.exam.ExamRecord;
import com.exam.bean.exam.Paper;
import com.exam.bean.exam.QuestionForAnswer;
import com.exam.dao.ExamDao;
import com.exam.mapper.exam.ExamMapper;
import com.exam.service.exam.ExamService;
import com.exam.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:config/application.xml"})
public class ExamTest {
    @Autowired
    private ExamService examService;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamDao examDao;

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
        exam.setId(1).setPaperIds("1").setHolderId(1).setTitle("测试考试").setMaxTimes(1)
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
        // 查询考试记录
        Utils.dump(examService.findExamRecord(1));
    }

    /**
     * 查找用户的指定考试的所有考试记录
     */
    @Test
    public void findExamInfo() {
        Utils.dump(examService.findExam(1, 1));
    }

    /**
     * 插入更新题目的作答 (Mongo)
     */
    @Test
    public void upsertQuestionAnswerMongo() {
        QuestionForAnswer answer = new QuestionForAnswer();
        answer.setExamRecordId(1).setQuestionId(1);
        QuestionForAnswer.QuestionOptionAnswer oa = new QuestionForAnswer.QuestionOptionAnswer();
        oa.setQuestionOptionId(1);
        oa.setContent("Hello");
        answer.getAnswers().add(oa);
        examDao.upsertQuestionAnswer(answer);
    }

    /**
     * 查询考试记录题目的作答
     */
    @Test
    public void findQuestionAnswersMongo() {
        long examRecordId = 1;
        List<QuestionForAnswer> answers = examDao.findQuestionForAnswersByExamRecordId(1);
        Utils.dump(answers);
    }

    @Test
    public void upsertExamRecordMongo() {
        ExamRecord record = new ExamRecord();
        record.setId(1).setUserId(1).setExamId(1).setStatus(2);

        Paper paper = new Paper();
        paper.setId(1001);
        record.setPaper(paper);

        // 选项和作答
        QuestionForAnswer.QuestionOptionAnswer a1 = new QuestionForAnswer.QuestionOptionAnswer();
        a1.setQuestionOptionId(11);
        QuestionForAnswer.QuestionOptionAnswer a2 = new QuestionForAnswer.QuestionOptionAnswer();
        a2.setQuestionOptionId(22);

        QuestionForAnswer q1 = new QuestionForAnswer();
        QuestionForAnswer q2 = new QuestionForAnswer();
        q1.setQuestionId(1).setScore(4.5);
        q2.setQuestionId(2).setScore(7.5);
        q1.getAnswers().add(a1);
        q2.getAnswers().add(a2);

        record.getQuestions().add(q1);
        record.getQuestions().add(q2);

        examDao.upsertExamRecord(record);
    }

    @Test
    public void findExamRecordMongo() {
        ExamRecord record = examDao.findExamRecordById(1);
        Utils.dump(record);
        Utils.dump(record.getQuestions());
    }

    @Test
    public void countExamRecordsMongo() {
        System.out.println(examDao.countExamRecordsByUserIdAndExamId(1, 1));
    }

    @Test
    public void findPaperIdsByUserIdAndExamIdMongo() {
        Utils.dump(examDao.findPaperIdsByUserIdAndExamId(1, 1));
    }

    @Test
    public void increaseExamRecordElapsedTimeMongo() {
        examDao.increaseExamRecordElapsedTime(1, 20);
        examDao.increaseExamRecordElapsedTime(1, 20);
    }
}
