package com.exam.service;

import com.exam.bean.exam.Question;
import com.exam.bean.exam.QuestionOption;
import com.exam.mapper.QuestionMapper;
import com.exam.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 操作题目的服务类，可以给题目自动分配 ID、添加选项、添加小题、CRUD 题目等
 */
@Slf4j
@Service
public class QuestionService extends BaseService {
    @Autowired
    private QuestionMapper questionMapper;

    /**
     * 插入或者更新题目，如果题目的 deleted 为 true，则会删除它
     *
     * @param question 题目
     * @return 返回题目的 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public long insertOrUpdateQuestion(Question question) {
        // 1. 确保题目的 ID
        // 2. 更新题目选项的 mark
        // 3. 更新题目的位置
        // 4. 如果 question.deleted 为 true，删除题目，返回
        // 5. 插入或者更新题目
        // 6. 删除、插入或者更新选项
        // 7. 插入或者更新小题

        // [1] 确保题目的 ID
        // [2] 更新题目选项的 mark
        // [3] 更新题目的位置
        ensureQuestionId(question);
        updateQuestionOptionMarks(question);
        updateQuestionPositions(question);

        // [4] 如果 question.deleted 为 true，删除题目，返回
        if (question.isDeleted()) {
            questionMapper.deleteQuestion(question.getId());
            return question.getId();
        }

        // [5] 插入或者更新题目
        questionMapper.insertOrUpdateQuestion(question);

        // [6] 删除、插入或者更新选项
        for (QuestionOption option : question.getOptions()) {
            if (option.isDeleted()) {
                // 删除选项
                questionMapper.deleteQuestionOption(option.getId());
            } else {
                // 插入或者更新选项
                questionMapper.insertOrUpdateQuestionOption(option);
            }
        }

        // [7] 插入或者更新小题 (递归)
        for (Question subQuestion : question.getSubQuestions()) {
            insertOrUpdateQuestion(subQuestion);
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
            ensureQuestionId(subQuestion);

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
            updateQuestionOptionMarks(subQuestion);
        }
    }

    /**
     * 更新题目的位置 (小题的位置, 选项的位置)
     *
     * @param question 题目
     */
    public void updateQuestionPositions(Question question) {
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
            updateQuestionPositions(subQuestion);
        }
    }
}
