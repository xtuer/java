package com.exam.service;

import com.exam.bean.exam.Paper;
import com.exam.bean.exam.Question;
import com.exam.mapper.PaperMapper;
import com.exam.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 操作试卷的服务
 */
@Service
public class PaperService extends BaseService {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private PaperMapper paperMapper;

    /**
     * 插入或者更新题目
     *
     * @param paper 试卷
     * @return 返回试卷的 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public long insertOrUpdatePaper(Paper paper) {
        // 1. 确保试卷的 ID
        // 2. 更新题目在试卷中的位置
        // 3. 插入或者更新题目到题目表 exam_question (题目自身的数据)
        // 4. 插入或者更新题目到试卷的题目表 exam_paper_question (题目和试卷的关系)
        // 5. 插入或者更新试卷表 exam_paper (试卷自身的数据)

        // [1] 确保试卷的 ID
        // [2] 更新题目在试卷中的位置
        ensurePaperId(paper);
        updatePaperQuestionPositions(paper);

        // [3] 插入或者更新题目到表 exam_question (题目自身的数据)
        for (Question question : paper.getQuestions()) {
            questionService.insertOrUpdateQuestion(question);
        }

        // [4] 插入或者更新题目到试卷的题目表 exam_paper_question (题目和试卷的关系)
        for (Question question : paper.getQuestions()) {
            insertOrUpdatePaperQuestion(question);
        }

        // [5] 插入或者更新试卷表 exam_paper (试卷自身的数据)
        paperMapper.insertOrUpdatePaper(paper);

        return paper.getId();
    }

    /**
     * 插入或者更新题目到试卷的题目表
     *
     * @param question 试卷的题目
     */
    public void insertOrUpdatePaperQuestion(Question question) {
        // 1. 如果题目标记为删除，则从表 exam_paper_question 中删除题目
        // 2. 插入或者更新题目到试卷题目表 exam_paper_question
        // 3. 插入或者更新小题到试卷题目表 exam_paper_question (小题也插入到试卷题目表，方便查询)

        // [1] 如果题目标记为删除，则从表 exam_paper_question 中删除题目
        if (question.isDeleted()) {
            // 删除试卷的题目
            paperMapper.deletePaperQuestion(question.getId());
            return;
        }

        // [2] 插入或者更新题目到试卷题目表 exam_paper_question
        paperMapper.insertOrUpdatePaperQuestion(question);

        // [3] 插入或者更新小题到试卷题目表 exam_paper_question (小题也插入到试卷题目表，方便查询)
        for (Question subQuestion : question.getSubQuestions()) {
            insertOrUpdatePaperQuestion(subQuestion);
        }
    }

    /**
     * 确保试卷的 ID 有效 (同时会确保相关题目的 ID、选项的 ID 有效)
     *
     * @param paper 试卷
     */
    public void ensurePaperId(Paper paper) {
        // 1. 确保试卷的 ID
        // 2. 确保题目的 ID
        // 3. 设置题目的试卷 ID

        // [1] 确保试卷的 ID
        if (Utils.isIdInvalid(paper.getId())) {
            paper.setId(nextId());
        }

        // [2] 确保题目的 ID
        for (Question question : paper.getQuestions()) {
            questionService.ensureQuestionId(question);
        }

        // [3] 设置题目的试卷 ID
        for (Question question : paper.getQuestions()) {
            question.setPaperId(paper.getId());

            for (Question subQuestion : question.getSubQuestions()) {
                subQuestion.setPaperId(paper.getId());
            }
        }
    }

    /**
     * 更新题目在试卷中的位置
     *
     * @param paper 试卷
     */
    public void updatePaperQuestionPositions(Paper paper) {
        // 提示: 只需要设置第一级题目的位置即可，小题和选项的位置由题目自己去保证
        int pos = 0;
        for (Question question : paper.getQuestions()) {
            question.setPosition(pos++);
        }
    }
}
