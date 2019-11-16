package com.exam.service.exam;

import com.exam.bean.Result;
import com.exam.bean.exam.Exam;
import com.exam.bean.exam.ExamRecord;
import com.exam.bean.exam.Paper;
import com.exam.mapper.exam.ExamMapper;
import com.exam.service.BaseService;
import com.exam.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 考试的服务
 *
 * 提示:
 *     1. 考试和用户无关，考试记录才和用户有关
 *     2. 同一个考试，同一个人可以创建多个考试记录，也就是考试允许多次作答
 *     3. 获取用户的某次考试记录时，得到的考试记录里会带上考试的试卷、作答内容，方便前端一次性得到所有数据
 */
@Slf4j
@Service
public class ExamService extends BaseService {
    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private PaperService paperService;

    /**
     * 插入或者更新考试
     *
     * @param exam 考试
     * @return 成功操作的 payload 为考试 ID
     */
    public Result<Long> upsertExam(Exam exam) {
        // 1. 检查考试时间，有效时间条件为:
        //    1.1 startTime < endTime
        //    2.2 endTime - startTime >= duration
        // 2. 检查最大次数: maxTimes >= 1
        // 3. 分配考试 ID
        // 4. 插入数据库，返回 Exam 的 ID

        // [1] 检查考试时间，有效时间条件为:
        //    [1.1] startTime < endTime
        //    [2.2] endTime - startTime >= duration
        if (exam.getStartTime().after(exam.getEndTime())) {
            return Result.failMessage("考试开始时间必须小于考试结束时间");
        }
        if ((exam.getEndTime().getTime() - exam.getStartTime().getTime()) < exam.getDuration() * 1000) {
            return Result.failMessage("考试时间区间必须大于考试持续时间");
        }

        // [2] 检查最大次数: maxTimes >= 1
        if (exam.getMaxTimes() < 1) {
            return Result.failMessage("最大考试次数必须大于等于 1");
        }

        // [3] 分配考试 ID
        if (Utils.isIdInvalid(exam.getId())) {
            exam.setId(super.nextId());
        }

        // [4] 插入数据库，返回 Exam 的 ID
        examMapper.upsertExam(exam);

        return Result.ok(exam.getId());
    }

    /**
     * 查询用户的考试记录，内容有: 考试记录信息、考试的试卷、用户的作答
     *
     * @param userId       用户 ID
     * @param examRecordId 考试记录 ID
     * @return 返回查询到的考试记录
     */
    public ExamRecord findExamRecord(long userId, long examRecordId) {
        // 1. 查找考试记录
        // 2. 查找试卷
        // 3. 查找作答
        // 4. 合并作答到试卷的题目选项里

        // [1] 查找考试记录
        ExamRecord record = examMapper.findExamRecordByUserIdAndExamRecordId(userId, examRecordId);

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
     * @return 成功创建考试记录时 payload 为考试记录 ID，否则返回错误的 Result 对象
     */
    public Result<Long> insertExamRecord(long userId, long examId) {
        // 1. 获取考试
        // 2. 检查考试状态，只有考试中时才允许创建考试记录
        // 3. 获取用户此考试的考试记录数量 recordCount
        // 4. 决定是否允许创建考试记录:
        //    4.1 如果 recordCount >= maxTimes，则不允许创建考试记录
        //    4.2 如果 recordCount < maxTimes，则允许创建考试记录，返回考试记录的 ID

        // [1] 获取考试
        Exam exam = examMapper.findExamById(examId);

        if (exam == null) {
            return Result.failMessage("考试不存在: " + examId);
        }

        // [2] 检查考试状态，只有考试中时才允许创建考试记录
        if (exam.getStatus() == Exam.STATUS_NOT_STARTED) {
            return Result.failMessage("考试未开始");
        }
        if (exam.getStatus() == Exam.STATUS_ENDED) {
            return Result.failMessage("考试已结束");
        }

        // [3] 获取用户此考试的考试记录数量 recordCount
        int recordCount = examMapper.countExamRecordsByUserIdAndExamId(userId, examId);

        // [4.1] 如果 recordCount >= maxTimes，则不允许创建考试记录
        if (recordCount >= exam.getMaxTimes()) {
            return Result.failMessage("考试次数已经用完");
        }

        // [4.2] 如果 recordCount < maxTimes，则允许创建考试记录，返回考试记录的 ID
        ExamRecord record = new ExamRecord();
        record.setId(super.nextId()).setUserId(userId).setExamId(examId).setPaperId(exam.getPaperId());
        examMapper.insertExamRecord(record);

        return Result.ok(record.getId());
    }

    /**
     * 判断用户是否可以进行考试作答 (作答是针对考试记录的)
     *
     * @param userId       用户 ID
     * @param examRecordId 考试记录 ID
     * @return 可作答返回 true，否则返回 false
     */
    public boolean canDoExamination(long userId, long examRecordId) {
        // 1. 查找用户的考试记录，不存在则返回 false
        // 2. 查找考试记录所属的考试
        // 3. 如果不在考试时间范围内则返回 false
        // 4. 如果考试时间已经用完则返回 false

        // [1] 查找用户的考试记录，不存在则返回 false
        ExamRecord record = examMapper.findExamRecordByUserIdAndExamRecordId(userId, examRecordId);

        if (record == null) {
            log.info("考试记录 {} 不存在，不能进行考试作答", examRecordId);
            return false;
        }

        // [2] 查找考试记录所属的考试 (考试记录依托于考试，所以理论上考试一定存在)
        Exam exam = examMapper.findExamById(record.getPaperId());

        // [3] 如果不在考试时间范围内，则返回 false
        if (exam.getStatus() != Exam.STATUS_STARTED) {
            return false;
        }

        // [4] 如果考试时间已经用完则返回 false
        if (record.getElapsedTime() >= exam.getDuration()) {
            return false;
        }

        return true;
    }
}
