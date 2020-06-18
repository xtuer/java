package com.xtuer.mapper.exam;

import com.xtuer.bean.exam.Exam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
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
     * 更新考试的基本信息 (名字、开始时间、结束时间、考试时间、最大考试次数)
     *
     * @param exam 考试
     */
    void updateExamBaseInfo(Exam exam);

    /**
     * 删除考试
     *
     * @param examId 考试 ID
     */
    void deleteExam(long examId);
}
