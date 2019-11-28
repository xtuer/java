package com.exam.mapper.exam;

import com.exam.bean.exam.Exam;

import java.util.List;

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
}
