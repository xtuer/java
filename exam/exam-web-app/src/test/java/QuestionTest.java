import com.exam.bean.exam.Question;
import com.exam.bean.exam.QuestionOption;
import com.exam.mapper.QuestionMapper;
import com.exam.service.IdWorker;
import com.exam.service.QuestionService;
import com.exam.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

// 测试题目
@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:config/application.xml"})
public class QuestionTest {
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionMapper questionMapper;

    // 测试创建选择题
    @Test
    public void createQuestion() {
        Question question = newQuestion(idWorker.nextId(), "单选题", Question.SINGLE_CHOICE);
        questionService.appendQuestionOption(question, newOption(idWorker.nextId(), "选项一"));
        questionService.appendQuestionOption(question, newOption(idWorker.nextId(), "选项二"));

        QuestionOption option = newOption(idWorker.nextId(), "选项三");
        questionService.appendQuestionOption(question, option);
        questionService.appendQuestionOption(question, option);

        Utils.dump(question);
    }

    // 测试创建复合题
    @Test
    public void createComplexQuestion() {
        Question question = newQuestion(idWorker.nextId(), "复合题", Question.COMPLEX);
        questionService.appendSubQuestion(question, newQuestion(idWorker.nextId(), "单选题", Question.SINGLE_CHOICE));
        questionService.appendSubQuestion(question, newQuestion(idWorker.nextId(), "判断题", Question.TFNG));

        Utils.dump(question);
    }

    // 测试为题目分配 ID
    @Test
    public void makeSureQuestionId() {
        Question question = newComposedQuestion();
        questionService.makeSureQuestionId(question);
        Utils.dump(question);
    }

    // 测试添加或者更新题目
    @Test
    public void insertOrUpdateQuestion() {
        // 提示: 测试前可先清空题目和选项表
        // delete from exam_question;
        // delete from exam_question_option;

        // 插入题目到数据库
        Question question = newComposedQuestion();
        questionService.insertOrUpdateQuestion(question);

        // 删除第一个选项
        // 删除第二个小题
        // 删除第一个小题的第二个选项
        // 然后更新数据库
        question.getOptions().get(0).setDeleted(true);
        question.getSubQuestions().get(1).setDeleted(true);
        question.getSubQuestions().get(0).getOptions().get(1).setDeleted(true);
        questionService.insertOrUpdateQuestion(question);
    }

    // 测试删除题目
    @Test
    public void deleteQuestion() {
        questionMapper.deleteQuestion(1);
    }

    // 创建题目
    static Question newQuestion(long id, String stem, int type) {
        Question question = new Question();
        question.setId(id).setStem(stem).setType(type);

        return question;
    }

    // 创建选项
    static QuestionOption newOption(long id, String description) {
        QuestionOption option = new QuestionOption();
        option.setId(id).setDescription(description);

        return option;
    }

    // 创建混合题
    public Question newComposedQuestion() {
        // 混合题
        //     选项一
        //     选项二
        //     选项三
        //
        //     小题一
        //         小题的选项一
        //         小题的选项二
        //         小题的选项三
        //     小题二
        //     小题三

        Question question = newQuestion(1, "混合题", Question.DESCRIPTION);

        // 选项
        questionService.appendQuestionOption(question, newOption(2, "选项一"));
        questionService.appendQuestionOption(question, newOption(0, "选项二"));
        questionService.appendQuestionOption(question, newOption(0, "选项三"));

        // 小题
        questionService.appendSubQuestion(question, newQuestion(3, "小题一", Question.SINGLE_CHOICE));
        questionService.appendSubQuestion(question, newQuestion(0, "小题二", Question.TFNG));
        questionService.appendSubQuestion(question, newQuestion(0, "小题三", Question.FITB));

        // 小题的选项
        questionService.appendQuestionOption(question.getSubQuestions().get(0), newOption(4, "小题的选项一"));
        questionService.appendQuestionOption(question.getSubQuestions().get(0), newOption(0, "小题的选项二"));
        questionService.appendQuestionOption(question.getSubQuestions().get(0), newOption(0, "小题的选项三"));

        return question;
    }
}
