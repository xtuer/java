import com.exam.bean.exam.Paper;
import com.exam.bean.exam.Question;
import com.exam.bean.exam.QuestionOption;
import com.exam.mapper.exam.QuestionMapper;
import com.exam.service.IdWorker;
import com.exam.service.exam.PaperService;
import com.exam.service.exam.QuestionService;
import com.exam.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/* 测试题目
 delete from exam_paper;
 delete from exam_paper_question;
 delete from exam_question;
 delete from exam_question_option;
 */
@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:config/application.xml"})
public class QuestionTest {
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private PaperService paperService;

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
        Utils.dump(newComposedQuestion());
    }

    // 测试为题目分配 ID
    @Test
    public void makeSureQuestionId() {
        Question question = newComposedQuestion();
        question.setId(0);
        question.getOptions().get(1).setId(0);
        question.getSubQuestions().get(1).setId(0);
        question.getSubQuestions().get(0).getOptions().get(1).setId(0);
        questionService.ensureQuestionId(question);
        Utils.dump(question);
    }

    // 测试添加或者更新题目
    @Test
    public void upsertQuestion() {
        // 提示: 测试前可先清空题目和选项表
        // delete from exam_question;
        // delete from exam_question_option;

        // 插入题目到数据库
        Question question = newComposedQuestion();
        questionService.upsertQuestion(question);

        // 删除第二个选项
        // 删除第二个小题
        // 删除第一个小题的第二个选项
        // 然后更新数据库
        question.getOptions().get(1).setDeleted(true);
        question.getSubQuestions().get(1).setDeleted(true);
        question.getSubQuestions().get(0).getOptions().get(1).setDeleted(true);
        questionService.upsertQuestion(question);
    }

    // 测试查询题目
    @Test
    public void findQuestionById() {
        Question question = questionService.findQuestionById(100);
        Utils.dump(question);
    }

    // 测试删除题目
    @Test
    public void deleteQuestion() {
        questionMapper.deleteQuestion(100);
    }

    // 测试编辑试卷
    @Test
    public void upsertPaper() {
        Paper paper = new Paper();
        paper.setId(100);
        paper.setTitle("测试试卷");
        paper.getQuestions().add(newQuestion(401, "试卷题目一", Question.FITB));
        paper.getQuestions().add(newQuestion(402, "试卷题目二", Question.FITB));
        paper.getQuestions().add(newComposedQuestion());

        // 创建试卷
        paperService.upsertPaper(paper);

        // 创建另一份试卷
        paper.setId(200);
        paperService.upsertPaper(paper);
    }

    // 测试查找试卷的题目
    @Test
    public void findPaper() {
        Paper paper = paperService.findPaper(100);
        Utils.dump(paper);
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

        Question question = newQuestion(100, "混合题", Question.COMPLEX);

        // 选项
        questionService.appendQuestionOption(question, newOption(201, "选项一"));
        questionService.appendQuestionOption(question, newOption(202, "选项二"));
        questionService.appendQuestionOption(question, newOption(203, "选项三"));

        // 小题
        questionService.appendSubQuestion(question, newQuestion(101, "小题一", Question.SINGLE_CHOICE));
        questionService.appendSubQuestion(question, newQuestion(102, "小题二", Question.TFNG));
        questionService.appendSubQuestion(question, newQuestion(103, "小题三", Question.FITB));

        // 小题的选项
        questionService.appendQuestionOption(question.getSubQuestions().get(0), newOption(301, "小题一的选项一"));
        questionService.appendQuestionOption(question.getSubQuestions().get(0), newOption(302, "小题一的选项二"));
        questionService.appendQuestionOption(question.getSubQuestions().get(0), newOption(303, "小题一的选项三"));

        return question;
    }
}
