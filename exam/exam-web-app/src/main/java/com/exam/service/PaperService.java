package com.exam.service;

import com.exam.bean.exam.Paper;
import com.exam.bean.exam.Question;
import com.exam.mapper.PaperMapper;
import com.exam.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 操作试卷的服务，关键接口有:
 *     查询试卷: findPaperById(paperId)
 *     更新试卷: upsertPaper(paper)
 */
@Service
public class PaperService extends BaseService {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private PaperMapper paperMapper;

    /**
     * 查找指定 ID 的试卷
     *
     * @param paperId 试卷 ID
     * @return 返回查找到的试卷，查不到返回 null
     */
    public Paper findPaperById(long paperId) {
        // 1. 查询试卷的基本信息
        // 2. 查询试卷的题目

        Paper paper = paperMapper.findPaperById(paperId);

        if (paper != null) {
            paper.setQuestions(questionService.findPaperQuestions(paperId));
        }

        return paper;
    }

    /**
     * 插入或者更新试卷
     *
     * @param paper 试卷
     * @return 返回试卷的 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public long upsertPaper(Paper paper) {
        // 1. 确保试卷的 ID
        // 2. 更新题目在试卷中的位置
        // 3. 更新题目在试卷中的序号 snLabel
        // 4. 计算试卷总分
        // 5. 插入或者更新题目到题目表 exam_question (题目自身的数据)
        // 6. 插入或者更新题目到试卷的题目表 exam_paper_question (题目和试卷的关系)
        // 7. 插入或者更新试卷表 exam_paper (试卷自身的数据)

        // [1] 确保试卷的 ID
        // [2] 更新题目在试卷中的位置
        // [3] 更新题目在试卷中的序号 snLabel
        // [4] 计算试卷总分
        this.ensurePaperId(paper);
        this.updatePaperQuestionPositions(paper);
        this.updatePaperQuestionSnLabels(paper.getQuestions());
        this.calculatePaperTotalScore(paper);

        // [5] 插入或者更新题目到表 exam_question (题目自身的数据)
        for (Question question : paper.getQuestions()) {
            questionService.upsertQuestion(question);
        }

        // [6] 插入或者更新题目到试卷的题目表 exam_paper_question (题目和试卷的关系)
        for (Question question : paper.getQuestions()) {
            this.upsertPaperQuestion(question);
        }

        // [7] 插入或者更新试卷表 exam_paper (试卷自身的数据)
        paperMapper.upsertPaper(paper);

        return paper.getId();
    }

    /**
     * 插入或者更新题目到试卷的题目表
     *
     * @param question 试卷的题目
     */
    public void upsertPaperQuestion(Question question) {
        // 1. 如果题目标记为删除，则从表 exam_paper_question 中删除题目
        // 2. 插入或者更新题目到试卷题目表 exam_paper_question
        // 3. 插入或者更新小题到试卷题目表 exam_paper_question (小题也插入到试卷题目表，方便查询)

        // [1] 如果题目标记为删除，则从表 exam_paper_question 中删除题目
        if (question.isDeleted()) {
            // 删除试卷的题目 (试卷题目表中小题的记录也被一起删除了)
            paperMapper.deletePaperQuestion(question.getId());
            return;
        }

        // [2] 插入或者更新题目到试卷题目表 exam_paper_question
        paperMapper.upsertPaperQuestion(question);

        // [3] 插入或者更新小题到试卷题目表 exam_paper_question (小题也插入到试卷题目表，方便查询)
        for (Question subQuestion : question.getSubQuestions()) {
            this.upsertPaperQuestion(subQuestion);
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
            question.setPositionInPaper(pos++);
        }
    }

    /**
     * 更新题目在试卷中的序号 snLabel
     *
     * @param questions 题目数组
     */
    public void updatePaperQuestionSnLabels(List<Question> questions) {
        // 1. 题型题: 使用中文序号，如 二、
        // 2. 普通题: 使用阿拉伯数字，如 2、
        // 3. 复合题的小题: 使用阿拉伯数字加括号，如（2）

        int gsn = 0; // 题型题
        int qsn = 0; // 普通题
        int ssn = 0; // 复合题的小题
        int groupSn = -1; // 当前分组序号

        for (Question question : questions) {
            // 忽略被删除的题目
            if (question.isDeleted()) {
                continue;
            }

            if (question.getGroupSn() != groupSn) {
                groupSn = question.getGroupSn();
                gsn += 1; // 新题型开始
            }

            // [1] 题型题: 使用中文序号，如 二、
            if (question.getType() == Question.DESCRIPTION) {
                question.setSnLabel(Utils.toCnNumber(gsn) + "、");
                return;
            }

            // [2] 普通题: 使用阿拉伯数字，如 2、
            qsn += 1;
            question.setSnLabel(qsn + "、");

            // [3] 复合题的小题: 使用阿拉伯数字加括号，如（2）
            ssn = 0;
            for (Question sub : question.getSubQuestions()) {
                // 忽略被删除的小题
                if (sub.isDeleted()) {
                    continue;
                }

                ssn += 1;
                sub.setSnLabel("(" + ssn + ")　");
            }
        }
    }

    /**
     * 计算试卷的满分
     *
     * @param paper 试卷
     * @return 返回试卷的满分
     */
    public double calculatePaperTotalScore(Paper paper) {
        // 试卷满分为一级可计分题目满分之和加上可计分小题的满分之和
        double total = 0;

        for (Question question : paper.getQuestions()) {
            if (this.canQuestionCalculateScore(question)) {
                total += question.getTotalScore();
            }

            for (Question sub : question.getSubQuestions()) {
                if (this.canQuestionCalculateScore(sub)) {
                    total += sub.getTotalScore();
                }
            }
        }

        return total;
    }

    /**
     * 判断题目是否可计分 (选择题、判断题、填空题、问答题可计分)
     *
     * @param question 题目
     * @return 如果题目可计分返回 true，否则返回 false
     */
    private boolean canQuestionCalculateScore(Question question) {
        return question.getType() == Question.SINGLE_CHOICE
                || question.getType() == Question.MULTIPLE_CHOICE
                || question.getType() == Question.FITB
                || question.getType() == Question.ESSAY_QUESTION;
    }
}
