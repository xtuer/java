package com.exam.mapper.exam;

import com.exam.bean.exam.Exam;
import com.exam.bean.exam.ExamRecord;

public interface ExamMapper {
    /**
     * 使用 ID 查找考试
     *
     * @param examId 考试 ID
     * @return 返回查找到的试卷，查找不到返回 null
     */
    Exam findExamById(long examId);

    /**
     * 创建或更新考试
     *
     * @param exam 考试
     */
    void upsertExam(Exam exam);

    /**
     * 查找用户的考试记录
     *
     * @param userId       用户 ID
     * @param examRecordId 考试记录 ID
     * @return 返回查找到的考试记录，查找不到返回 null
     */
    ExamRecord findExamRecordByUserIdAndExamRecordId(long userId, long examRecordId);

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
}
