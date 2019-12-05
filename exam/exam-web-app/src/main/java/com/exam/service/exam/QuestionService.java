package com.exam.service.exam;

import com.exam.bean.exam.Question;
import com.exam.bean.exam.QuestionOption;
import com.exam.mapper.exam.QuestionMapper;
import com.exam.service.BaseService;
import com.exam.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 操作题目的服务类，可以给题目自动分配 ID、添加选项、添加小题、CRUD 题目等，关键接口有:
 *     查询题目: findQuestion(questionId)
 *     更新题目: upsertQuestion(question)
 *     试卷题目: findPaperQuestions(paperId)
 */
@Slf4j
@Service
public class QuestionService extends BaseService {
    @Autowired
    private QuestionMapper questionMapper;

    /**
     * 查找指定 ID 的题目
     *
     * @param questionId 题目 ID
     * @return 返回查找到的题目，找不到返回 null
     */
    public Question findQuestion(long questionId) {
        // 1. 查找 ID 为 questionId 的题目，并且查询出它的小题
        // 2. 把小题放置到 subQuestions 中
        List<Question> questions = questionMapper.findQuestionById(questionId);
        questions = this.hierarchyQuestions(questions);

        return questions.size() > 0 ? questions.get(0) : null;
    }

    /**
     * 查找试卷的题目 (复合题的小题被合并到复合题下了)
     *
     * @param paperId 试卷 ID
     * @return 返回题目的数组
     */
    public List<Question> findPaperQuestions(long paperId) {
        List<Question> questions = questionMapper.findPaperQuestions(paperId);
        return this.hierarchyQuestions(questions);
    }

    /**
     * 把平铺的题目层级化 (复合题的小题被合并到复合题下了)
     *
     * @param questions 题目数组
     * @return 返回层级化的题目数组
     */
    private List<Question> hierarchyQuestions(List<Question> questions) {
        // 1. 找到第一级题目
        // 2. 找到第二级题目 (小题: parentId 有效 ID)
        // 3. 把一级题目放到 Map，加速查找
        // 4. 遍历所有小题，把小题放到所属的题目下

        List<Question> topQuestions = questions.stream().filter(q -> Utils.isIdInvalid(q.getParentId())).collect(Collectors.toList());
        List<Question> subQuestions = questions.stream().filter(q -> Utils.isIdValid(q.getParentId())).collect(Collectors.toList());
        Map<Long, Question> topQuestionMap = topQuestions.stream().collect(Collectors.toMap(Question::getId, question -> question));

        // [4] 遍历所有小题，把小题放到所属的题目下
        for (Question sub : subQuestions) {
            Question top = topQuestionMap.get(sub.getParentId()); // 查找小题所属题目

            if (top != null) {
                top.getSubQuestions().add(sub); // 把小题放到对应的题目下
            }
        }

        return topQuestions;
    }

    /**
     * 插入或者更新题目，如果题目的 deleted 为 true，则会删除它
     *
     * @param question 题目
     * @return 返回题目的 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public long upsertQuestion(Question question) {
        // 1. 确保题目的 ID
        // 2. 更新题目选项的 mark
        // 3. 更新小题和选项的位置
        // 4. 如果 question.deleted 为 true，删除题目，返回
        // 5. 插入或者更新题目
        // 6. 去掉被删除的选项
        // 7. 插入或者更新小题

        // [1] 确保题目的 ID
        // [2] 更新题目选项的 mark
        // [3] 更新小题和选项的位置
        this.ensureQuestionId(question);
        this.updateQuestionOptionMarks(question);
        this.updateSubQuestionAndOptionPositions(question);

        // [4] 如果 question.deleted 为 true，删除题目，返回
        if (question.isDeleted()) {
            questionMapper.deleteQuestion(question.getId());
            return question.getId();
        }

        // [5] 插入或者更新题目
        questionMapper.upsertQuestion(question);

        // [6] 去掉被删除的选项
        question.setOptions(question.getOptions().stream().filter(o -> !o.isDeleted()).collect(Collectors.toList()));

        // [7] 插入或者更新小题 (递归)
        for (Question subQuestion : question.getSubQuestions()) {
            upsertQuestion(subQuestion);
        }

        return question.getId();
    }

    /**
     * 给题目添加选项
     *
     * @param question 题目
     * @param option   选项
     */
    public void appendQuestionOption(Question question, QuestionOption option) {
        // 1. 在题目的选项中查找此选项是否存在
        // 2. 选项不存在才添加，设置选项的 questionId
        // 3. 矫正选项的顺序

        // [1] 在题目的选项中查找此选项是否存在
        boolean found = false;
        for (QuestionOption o : question.getOptions()) {
            if (Utils.isIdValid(option.getId()) && o.getId() == option.getId()) {
                found = true;
                break;
            }
        }

        if (found) {
            log.info("选项 {} 已经存在，不重复添加", option.getId());
            return;
        }

        // [2] 选项不存在才添加，设置选项的 questionId
        option.setQuestionId(question.getId());
        question.getOptions().add(option);

        // [3] 矫正选项的顺序
        int pos = 0;
        for (QuestionOption o : question.getOptions()) {
            o.setPosition(pos++);
        }
    }

    /**
     * 给复合题 question 添加小题 subQuestion
     *
     * @param question    复合题
     * @param subQuestion 小题
     */
    public void appendSubQuestion(Question question, Question subQuestion) {
        // 1. 在题目的小题中查找此小题是否存在
        // 2. 小题不存在才添加，设置小题的 parentId
        // 3. 矫正小题的顺序

        // [1] 在题目的小题中查找此小题是否存在
        boolean found = false;
        for (Question q : question.getSubQuestions()) {
            if (Utils.isIdValid(subQuestion.getId()) && q.getId() == subQuestion.getId()) {
                found = true;
                break;
            }
        }

        if (found) {
            log.info("小题 {} 已经存在，不重复添加", subQuestion.getId());
            return;
        }

        // [2] 小题不存在才添加，设置小题的 parentId
        subQuestion.setParentId(question.getId());
        question.getSubQuestions().add(subQuestion);

        // [3] 矫正小题的顺序
        int pos = 0;
        for (Question q : question.getSubQuestions()) {
            q.setPosition(pos++);
        }
    }

    /**
     * 确保题目的 ID 有效 (包含选项 ID，小题 ID，所属题目 ID 等)，如果 ID 有效则继续使用，无效则为其分配一个 ID
     *
     * @param question 题目
     */
    public void ensureQuestionId(Question question) {
        // 1. 确保题目的 ID
        // 2. 确保选项的 ID
        // 3. 确保选项的题目 ID
        // 4. 确保复合题小题的 ID
        // 5. 确保复合题小题所属题目的 ID

        // [1] 确保题目的 ID
        if (Utils.isIdInvalid(question.getId())) {
            question.setId(nextId());
        }

        for (QuestionOption option : question.getOptions()) {
            // [1] 确保选项的 ID
            if (Utils.isIdInvalid(option.getId())) {
                option.setId(nextId());
            }

            // [3] 确保选项的题目 ID
            option.setQuestionId(question.getId());
        }

        for (Question subQuestion : question.getSubQuestions()) {
            // [4] 确保复合题小题的 ID
            this.ensureQuestionId(subQuestion);

            // [5] 确保复合题小题所属题目的 ID
            subQuestion.setParentId(question.getId());
        }
    }

    /**
     * 更新题目选项的 mark (A, B, C, D)
     *
     * @param question 题目
     */
    public void updateQuestionOptionMarks(Question question) {
        // 1. 更新题目的选项 mark
        // 2. 更新小题的选项 mark

        // [1] 更新题目的选项 mark
        char sn = 65;
        for (QuestionOption option : question.getOptions()) {
            if (!option.isDeleted()) {
                option.setMark("" + (sn++));
            }
        }

        // [2] 更新小题的选项 mark
        for (Question subQuestion : question.getSubQuestions()) {
            this.updateQuestionOptionMarks(subQuestion);
        }
    }

    /**
     * 更新小题和选项的位置
     *
     * @param question 题目
     */
    public void updateSubQuestionAndOptionPositions(Question question) {
        // 1. 更新选项的位置
        // 2. 更新小题的位置
        // 3. 更新小题选项的位置

        // [1] 更新选项的位置
        int optionPos = 0;
        for (QuestionOption option : question.getOptions()) {
            if (!option.isDeleted()) {
                option.setPosition(optionPos++);
            }
        }

        int subPos = 0;
        for (Question subQuestion : question.getSubQuestions()) {
            if (subQuestion.isDeleted()) {
                continue;
            }

            // [2] 更新小题的位置
            subQuestion.setPosition(subPos++);

            // [3] 更新小题选项的位置
            this.updateSubQuestionAndOptionPositions(subQuestion);
        }
    }

    /**
     * 获取题目下的所有选项 (复合题时为所有小题的选项)
     *
     * @param question 题目
     * @return 返回小题选项的数组
     */
    public List<QuestionOption> getQuestionOptions(Question question) {
        if (question.getType() == Question.COMPOSITE) {
            return question.getSubQuestions().stream().map(Question::getOptions).flatMap(List::stream).collect(Collectors.toList());
        } else {
            return new LinkedList<>(question.getOptions());
        }
    }
}
