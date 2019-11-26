package com.exam.mapper.exam;

import com.exam.bean.exam.Exam;
import com.exam.bean.exam.ExamRecord;
import com.exam.bean.exam.QuestionOptionAnswer;
import com.exam.bean.exam.QuestionResult;

import java.util.List;
import java.util.Set;

public interface ExamMapper {
    /**
     * 使用 ID 查找考试
     *
     * @param examId 考试 ID
     * @return 返回查找到的试卷，查找不到返回 null
     */
    Exam findExamById(long examId);

    /**
     * 查找 ID 为传入的 orgId 的机构下的考试
     *
     * @param orgId 机构 ID
     * @return 返回查找到的考试数组
     */
    List<Exam> findExamsByOrgId(long orgId);

    /**
     * 创建或更新考试
     *
     * @param exam 考试
     */
    void upsertExam(Exam exam);

    /**
     * 查找考试记录
     *
     * @param examRecordId 考试记录 ID
     * @return 返回查找到的考试记录，查找不到返回 null
     */
    ExamRecord findExamRecordById(long examRecordId);

    /**
     * 查找用户的指定考试的所有考试记录
     *
     * @param userId 用户 ID
     * @param examId 考试 ID
     * @return 返回查找到的考试记录数组
     */
    List<ExamRecord> findExamRecordsByUserIdAndExamId(long userId, long examId);

    /**
     * 统计用户某次考试的考试记录数量
     *
     * @param userId 用户 ID
     * @param examId 考试 ID
     * @return 返回考试记录数量
     */
    int countExamRecordsByUserIdAndExamId(long userId, long examId);

    /**
     * 创建考试记录
     *
     * @param record 考试记录
     */
    void insertExamRecord(ExamRecord record);

    /**
     * 查找用户在某次考试中已经做过的试卷
     *
     * @param userId 用户 ID
     * @param examId 考试 ID
     * @return 返回试卷 ID 的数组
     */
    Set<Long> findPaperIdsByUserIdAndExamId(long userId, long examId);

    /**
     * 更新考试记录的状态
     *
     * @param examRecordId 考试记录 ID
     * @param status       状态
     */
    void updateExamRecordStatus(long examRecordId, int status);

    /**
     * 增加考试记录的使用时间
     *
     * @param examRecordId 考试记录 ID
     * @param timeInSeconds 增加的时间
     */
    void increaseExamRecordElapsedTime(long examRecordId, long timeInSeconds);

    /**
     * 插入题目的回答
     *
     * @param answer 回答
     */
    void insertQuestionOptionAnswer(QuestionOptionAnswer answer);

    /**
     * 查找指定考试记录所有题目选项的回答
     *
     * @param examRecordId 考试记录 ID
     * @return 返回回答的数组
     */
    List<QuestionOptionAnswer> findQuestionOptionAnswersByExamRecordId(long examRecordId);

    /**
     * 删除指定的考试记录里指定题目的所有回答
     *
     * @param examRecordId 考试记录 ID
     * @param questionId   题目 ID
     */
    void deleteQuestionOptionAnswersByExamRecordIdAndQuestionId(long examRecordId, long questionId);

    /**
     * 插入或者更新题目的作答结果
     *
     * @param result 题目的作答结果
     */
    void upsertQuestionResult(QuestionResult result);

    /**
     * 查询考试记录中题目的得分
     *
     * @param examRecordId 考试记录 ID
     * @return 返回题目得分的数组
     */
    List<QuestionResult> findQuestionResultByExamRecordId(long examRecordId);
}
