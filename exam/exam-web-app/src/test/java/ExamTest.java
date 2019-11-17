import com.exam.bean.exam.Exam;
import com.exam.bean.exam.ExamRecordAnswer;
import com.exam.bean.exam.QuestionOptionAnswer;
import com.exam.mapper.exam.ExamMapper;
import com.exam.service.exam.ExamService;
import com.exam.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
     * 插入更新题目的回答
     */
    @Test
    public void answerExamRecord() {
        long recordId = 1;
        ExamRecordAnswer examRecordAnswer = new ExamRecordAnswer();
        examRecordAnswer.setExamRecordId(recordId).setSubmitted(true);

        List<QuestionOptionAnswer> answers = new LinkedList<>();
        answers.add(newAnswer(recordId, 1, 1, ""));
        answers.add(newAnswer(recordId, 1, 2, ""));
        answers.add(newAnswer(recordId, 2, 3, "填空-1"));
        answers.add(newAnswer(recordId, 2, 4, "填空-2"));
        answers.add(newAnswer(recordId, 3, 5, "回答"));

        examRecordAnswer.setAnswers(answers);

        Utils.dump(examService.answerExamRecord(examRecordAnswer));
    }

    @Test
    public void findQuestionOptionAnswersByExamRecordId() {
        Utils.dump(examMapper.findQuestionOptionAnswersByExamRecordId(1));
    }

    /**
     * 创建题目选项的回答
     */
    private QuestionOptionAnswer newAnswer(long examRecordId, long questionId, long questionOptionId, String content) {
        QuestionOptionAnswer answer = new QuestionOptionAnswer();
        answer.setExamRecordId(examRecordId).setQuestionId(questionId).setQuestionOptionId(questionOptionId).setContent(content);

        return answer;
    }
}
