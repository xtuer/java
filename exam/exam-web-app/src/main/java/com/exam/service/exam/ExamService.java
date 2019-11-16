package com.exam.service.exam;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.Cached;
import com.exam.bean.CacheConst;
import com.exam.bean.Result;
import com.exam.bean.exam.*;
import com.exam.mapper.exam.ExamMapper;
import com.exam.service.BaseService;
import com.exam.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考试的服务:
 *     创建或编辑考试
 *     创建考试记录
 *     查找考试记录 (考试记录信息、考试的试卷、用户的作答)
 *     考试作答
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

    @Autowired
    private ExamService self;

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

        // [5] 清楚考试的缓存
        self.invalidateExamCache(exam.getId());

        return Result.ok(exam.getId());
    }

    /**
     * 使用 ID 查找考试
     *
     * @param examId 考试 ID
     * @return 返回查找到的考试，查找不到返回 null
     */
    @Cached(name = CacheConst.CACHE, key = CacheConst.KEY_EXAM)
    public Exam findExam(long examId) {
        return examMapper.findExamById(examId);
    }

    /**
     * 清楚考试的缓存
     *
     * @param examId 考试 ID
     */
    @CacheInvalidate(name = CacheConst.CACHE, key = CacheConst.KEY_EXAM)
    public void invalidateExamCache(long examId) {

    }

    /**
     * 查询用户的考试记录，内容有: 考试记录信息、考试的试卷、用户的作答
     *
     * @param examRecordId 考试记录 ID
     * @return 返回查询到的考试记录
     */
    public ExamRecord findExamRecord(long examRecordId) {
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
        List<QuestionOptionAnswer> examAnswers = examMapper.findQuestionOptionAnswersByExamRecordId(examRecordId);
        Map<Long, QuestionOptionAnswer> answersMap = examAnswers.stream().collect(Collectors.toMap(QuestionOptionAnswer::getQuestionOptionId, a -> a));

        for (Question question : paper.getQuestions()) {
            // 题目的选项
            for (QuestionOption option : question.getOptions()) {
                QuestionOptionAnswer answer = answersMap.get(option.getId());

                if (answer != null) {
                    option.setChecked(true);
                    option.setAnswer(answer.getContent());
                }
            }

            for (Question subQuestion : question.getSubQuestions()) {
                // 小题的选项
                for (QuestionOption option : subQuestion.getOptions()) {
                    QuestionOptionAnswer answer = answersMap.get(option.getId());

                    if (answer != null) {
                        option.setChecked(true);
                        option.setAnswer(answer.getContent());
                    }
                }
            }
        }

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
        // 1. 查找考试
        // 2. 检查考试状态，只有考试中时才允许创建考试记录
        // 3. 获取用户此考试的考试记录数量 recordCount
        // 4. 决定是否允许创建考试记录:
        //    4.1 如果 recordCount >= maxTimes，则不允许创建考试记录
        //    4.2 如果 recordCount < maxTimes，则允许创建考试记录，返回考试记录的 ID

        // [1] 查找考试
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
     * 考试作答
     *
     * @param examRecordAnswer 作答
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> answerExamRecord(ExamRecordAnswer examRecordAnswer) {
        // 1. 查询考试记录
        // 2. 如果不能作答则返回
        // 3. 更新考试记录的状态:
        //    3.1 submitted 为 true 表示提交试卷，更新考试记录状态为 2 (已提交)
        //    3.2 submitted 为 false 表示普通作答，更新考试记录状态为 1 (已作答)
        // 4. 把回答按题目分组
        // 5. 删除题目的所有回答
        // 6. 创建回答

        // [1] 查询考试记录
        long examRecordId = examRecordAnswer.getExamRecordId();
        ExamRecord examRecord = examMapper.findExamRecordById(examRecordId);

        // [2] 如果不能作答则返回
        Result<Boolean> result = canDoExamination(examRecord, examRecordId);
        if (!result.isSuccess()) {
            return result;
        }

        // [4] 更新考试记录的状态
        if (examRecordAnswer.isSubmitted()) {
            examMapper.updateExamRecordStatus(examRecordId, ExamRecord.STATUS_SUBMITTED);
        } else {
            examMapper.updateExamRecordStatus(examRecordId, ExamRecord.STATUS_ANSWERED);
        }

        // [4] 把回答按题目分组
        List<QuestionOptionAnswer> answers = examRecordAnswer.getAnswers();
        Map<Long, List<QuestionOptionAnswer>> answersMap = answers.stream().collect(Collectors.groupingBy(QuestionOptionAnswer::getQuestionId));

        answersMap.forEach((questionId, questionAnswers) -> {
            // [5] 删除题目的所有回答
            examMapper.deleteQuestionOptionAnswersByExamRecordIdAndQuestionId(examRecordId, questionId);

            // [6] 创建回答
            for (QuestionOptionAnswer answer : questionAnswers) {
                answer.setExamRecordId(examRecordId); // 再次确保考试记录 ID，避免前端忘了填
                examMapper.insertQuestionOptionAnswer(answer);
            }
        });

        return Result.ok();
    }

    /**
     * 判断是否可以进行考试作答 (作答是针对考试记录的)
     *
     * @param examRecord   考试记录
     * @param examRecordId 考试记录 ID
     * @return 可作答返回 Result.ok()，否则返回错误说明
     */
    public Result<Boolean> canDoExamination(ExamRecord examRecord, long examRecordId) {
        // 1. 如果考试记录为 null，不能作答
        // 2. 考试记录已提交不能作答，不能作答
        // 3. 查找考试记录所属的考试 (考试记录依托于考试，所以理论上考试一定存在)
        // 4. 如果不在考试时间范围内，不能作答
        // 5. 如果考试时间已经用完，不能作答
        // 6. 其他情况均可作答

        // [1] 如果考试记录为 null，不能作答
        if (examRecord == null) {
            return Result.failMessage("考试记录 " + examRecordId + " 不存在，不能作答");
        }

        // [2] 考试记录已提交不能作答，不能作答
        if (examRecord.getStatus() >= ExamRecord.STATUS_SUBMITTED) {
            return Result.failMessage("考试记录 " + examRecordId + " 已经提交，不能再作答");
        }

        // [3] 查找考试记录所属的考试 (考试记录依托于考试，所以理论上考试一定存在)
        Exam exam = self.findExam(examRecord.getExamId()); // 从缓存中查询考试

        // [4] 如果不在考试时间范围内，不能作答
        if (exam.getStatus() != Exam.STATUS_STARTED) {
            return Result.failMessage("不在考试时间范围内，不能作答");
        }

        // [5] 如果考试时间已经用完，不能作答
        if (examRecord.getElapsedTime() >= exam.getDuration()) {
            return Result.failMessage("考试时间已经用完，不能作答");
        }

        // [6] 其他情况均可作答
        return Result.ok();
    }
}
