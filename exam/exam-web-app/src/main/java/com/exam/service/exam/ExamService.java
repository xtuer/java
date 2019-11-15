package com.exam.service.exam;

import com.exam.bean.Result;
import com.exam.bean.exam.Exam;
import com.exam.bean.exam.ExamRecord;
import com.exam.bean.exam.Paper;
import com.exam.mapper.exam.ExamMapper;
import com.exam.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 考试的服务
 */
@Slf4j
@Service
public class ExamService extends BaseService {
    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private PaperService paperService;

    /**
     * 查询用户的某个考试记录，内容有: 考试记录信息、试卷、作答
     *
     * @param userId       用户 ID
     * @param examRecordId 记录 ID
     * @return payload 为
     */
    public ExamRecord findExamRecord(long userId, long examRecordId) {
        // 1. 查找考试记录
        // 2. 查找试卷
        // 3. 查找作答
        // 4. 合并作答到试卷的题目选项里

        // [1] 查找考试记录
        ExamRecord record = examMapper.findExamRecordById(examRecordId);

        // [2] 查找试卷
        Paper paper = paperService.findPaperById(record.getPaperId());
        record.setPaper(paper);

        // [3] 查找作答
        // [4] 合并作答到试卷的题目选项里

        return record;
    }

    /**
     * 创建用户某次考试的考试记录
     *
     * @param userId 用户 ID
     * @param examId 考试 ID
     * @return 成功创建考试记录时 payload 为考试记录 ID，否则为错误说明
     */
    public Result<Long> insertExamRecord(long userId, long examId) {
        // 1. 获取考试信息，得到最大考试次数 maxTimes
        // 2. 获取已有的考试记录数量 recordCount
        // 3. 决定是否允许创建考试记录:
        //    3.1 如果 recordCount >= maxTimes，则不允许创建考试记录
        //    3.2 如果 recordCount < maxTimes，则允许创建考试记录，返回考试记录的 ID

        // [1] 获取考试信息，得到最大考试次数 maxTimes
        Exam exam = examMapper.findExamById(examId);

        if (exam == null) {
            return Result.failMessage("考试不存在: " + examId);
        }

        // [2] 获取已有的考试记录数量 recordCount
        int recordCount = examMapper.countExamRecords(userId, examId);

        // [3.1] 如果 recordCount >= maxTimes，则不允许创建考试记录
        if (recordCount >= exam.getMaxTimes()) {
            return Result.failMessage("考试次数已经用完");
        }

        // [3.2] 如果 recordCount < maxTimes，则允许创建考试记录，返回考试记录的 ID
        ExamRecord record = new ExamRecord();
        record.setId(super.nextId()).setUserId(userId).setExamId(examId).setPaperId(exam.getPaperId());
        examMapper.insertExamRecord(record);

        return Result.ok(record.getId());
    }
}
