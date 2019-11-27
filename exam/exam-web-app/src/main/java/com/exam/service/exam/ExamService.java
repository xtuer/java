package com.exam.service.exam;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.Cached;
import com.exam.bean.CacheConst;
import com.exam.bean.Result;
import com.exam.bean.exam.*;
import com.exam.mapper.exam.ExamMapper;
import com.exam.mapper.exam.PaperMapper;
import com.exam.service.BaseService;
import com.exam.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 考试的服务:
 *     创建或编辑考试: upsertExam
 *     用户的考试信息: findExam(userId, examId) (包含考试记录)
 *     创建考试记录: insertExamRecord(userId, examId)
 *     查找考试记录: findExamRecord(recordId) (考试记录信息、考试的试卷、用户的作答)
 *     考试作答: answerExamRecord(answer)
 *
 * 提示:
 *     1. 考试和用户无关，考试记录才和用户有关，由于考试读多写少，可以放入缓存
 *     2. 同一个考试，同一个人可以创建多个考试记录，也就是考试允许多次作答
 *     3. 获取用户的某次考试记录时，得到的考试记录里会带上考试的试卷、作答内容，方便前端一次性得到所有数据
 *
 * 缓存:
 *     1. 使用 ID 查找考试  : ExamService.findExam(examId)
 *     2. 查找指定 ID 的试卷: PaperService.findPaper(paperId)
 *
 * 优化:
 *     考试作答: answerExamRecord
 *         1. 操作数据库比较多，后期可放到 MQ
 *         2. 由于是一次性获取考试记录的作答，不需要使用关联查询，可以存储到 MongoDB
 *         3. 中间的作答保存到 MongoDB，目前只保存了最后一次的作答
 */
@Slf4j
@Service
public class ExamService extends BaseService {
    private static final int SUBMIT_DELAY = 10; // 考试提交允许延迟的时间, 10 秒 (因为网络传输的延迟等，需要延迟一点时间进行校正，不能非常精确)

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private PaperService paperService;

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private ExamService self;

    /**
     * 插入或者更新考试
     *
     * @param exam 考试
     * @return 成功操作的 payload 为考试 ID
     */
    @CacheInvalidate(name = CacheConst.CACHE, key = CacheConst.KEY_EXAM)
    public Result<Long> upsertExam(Exam exam) {
        // 1. 分配试卷 ID
        // 2. 考试标题不能为空
        // 3. 检查考试时间，有效时间条件为:
        //    3.1 startTime < endTime
        //    3.2 endTime - startTime >= duration
        // 4. 检查最大次数: maxTimes >= 1
        // 5. 试卷必须存在
        // 6. 插入数据库
        // 7. 返回考试 ID

        // [1] 分配考试 ID
        if (Utils.isIdInvalid(exam.getId())) {
            exam.setId(super.nextId());
        }

        // [2] 考试标题不能为空
        if (StringUtils.isBlank(exam.getTitle())) {
            return Result.failMessage("考试的标题不能为空");
        }

        // [3] 检查考试时间，有效时间条件为:
        //    [3.1] startTime < endTime
        //    [3.2] endTime - startTime >= duration
        if (exam.getStartTime().after(exam.getEndTime())) {
            return Result.failMessage("考试开始时间必须小于考试结束时间");
        }
        if ((exam.getEndTime().getTime() - exam.getStartTime().getTime()) < exam.getDuration() * 1000) {
            return Result.failMessage("考试时间区间必须大于考试持续时间");
        }

        // [4] 检查最大次数: maxTimes >= 1
        if (exam.getMaxTimes() < 1) {
            return Result.failMessage("最大考试次数必须大于等于 1");
        }

        // [5] 试卷必须存在
        Set<Long> paperIds = exam.getPaperIdsList();

        if (paperIds.isEmpty()) {
            return Result.failMessage("没有试卷");
        }

        for (long paperId : paperIds) {
            if (!paperMapper.paperExists(paperId)) {
                log.warn("[失败] 创建考试: 试卷 {} 不存在", paperId);
                return Result.failMessage("试卷 " +paperId + " 不存在");
            }
        }

        // [6] 插入数据库
        examMapper.upsertExam(exam);
        log.info("[成功] 创建试卷: 试卷 ID {}，标题: {}", exam.getId(), exam.getTitle());

        // [7] 返回考试 ID
        return Result.ok(exam.getId());
    }

    /**
     * 使用 ID 查找考试
     *
     * @param examId 考试 ID
     * @return 返回查找到的考试，查找不到返回 null
     */
    @Cached(name = CacheConst.CACHE, key = CacheConst.KEY_EXAM_ID)
    public Exam findExam(long examId) {
        return examMapper.findExamById(examId);
    }

    /**
     * 查找用户的考试信息，如果用户在此考试中进行过作答，同时查找出所有相关的考试记录
     *
     * @param userId 用户 ID
     * @param examId 考试 ID
     * @return 返回考试
     */
    public Exam findExam(long userId, long examId) {
        Exam exam = self.findExam(examId);
        exam.setExamRecords(examMapper.findExamRecordsByUserIdAndExamId(userId, examId));

        return exam;
    }

    /**
     * 查询用户的考试记录，内容有: 考试、试卷、考试记录、此考试记录的用户作答 (已经合并到试卷里)
     * 提示: 不需要传入用户 ID，因为考试记录是和用户一一对应的
     *
     * @param examRecordId 考试记录 ID
     * @return 返回查询到的考试记录
     */
    public ExamRecord findExamRecord(long examRecordId) {
        // 1. 查找考试记录
        // 2. 查找考试和试卷
        // 3. 查找作答
        // 4. 合并作答到试卷的题目选项里
        // 5. 批改客观题: 未批改、并且不能继续作答的考试记录的客观题
        // 6. 查询题目得分

        // [1] 查找考试记录
        ExamRecord record = examMapper.findExamRecordById(examRecordId);

        // [2] 查找考试和试卷
        Exam  exam  = self.findExam(record.getExamId());
        Paper paper = paperService.findPaper(record.getPaperId());
        record.setExam(exam);
        record.setPaper(paper);

        // 所有题目
        List<Question> questions = new LinkedList<>();
        questions.addAll(paper.getQuestions());
        questions.addAll(paper.getQuestions().stream().flatMap(q -> q.getSubQuestions().stream()).collect(Collectors.toList()));

        // [3] 查找作答
        List<QuestionOptionAnswer> examAnswers = examMapper.findQuestionOptionAnswersByExamRecordId(examRecordId);
        Map<Long, QuestionOptionAnswer> answersMap = examAnswers.stream().collect(Collectors.toMap(QuestionOptionAnswer::getQuestionOptionId, a -> a));

        // [4] 合并作答到试卷的题目选项里
        questions.stream().flatMap(q -> q.getOptions().stream()).forEach(option -> {
            QuestionOptionAnswer answer = answersMap.get(option.getId());

            if (answer != null) {
                option.setChecked(true);
                option.setAnswer(answer.getContent());
            }
        });

        // [5] 批改客观题: 未批改、并且不能继续作答的考试记录的客观题
        if (record.getStatus() < ExamRecord.STATUS_AUTO_CORRECTED && !this.canDoExamination(record.getUserId(), record.getId(), record).isSuccess()) {
            this.correctObjectiveQuestions(record);
        }

        // [6] 查询题目得分
        if (record.getStatus() >= ExamRecord.STATUS_AUTO_CORRECTED) {
            List<QuestionResult> questionResults = examMapper.findQuestionResultByExamRecordId(examRecordId);
            Map<Long, QuestionResult> questionResultsMap = questionResults.stream().collect(Collectors.toMap(QuestionResult::getQuestionId, r -> r));
            questions.forEach(question -> {
                QuestionResult result =  questionResultsMap.get(question.getId());

                if (result != null) {
                    question.setScore(result.getScore());
                    question.setScoreStatus(result.getStatus());
                }
            });
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
        // 4. 如果 recordCount >= maxTimes，则不允许创建考试记录
        // 5. 分配试卷
        // 6. 创建考试记录，返回考试记录的 ID

        // [1] 查找考试
        Exam exam = self.findExam(examId);

        if (exam == null) {
            log.warn("[失败] 创建考试记录: 用户 {}, 考试 {}，考试不存在", userId, examId);
            return Result.failMessage("考试不存在: " + examId);
        }

        // [2] 检查考试状态，只有考试中时才允许创建考试记录
        if (exam.getStatus() == Exam.STATUS_NOT_STARTED) {
            log.warn("[失败] 创建考试记录: 用户 {}, 考试 {}，考试未开始", userId, examId);
            return Result.failMessage("考试未开始");
        }
        if (exam.getStatus() == Exam.STATUS_ENDED) {
            log.warn("[失败] 创建考试记录: 用户 {}, 考试 {}，考试已结束", userId, examId);
            return Result.failMessage("考试已结束");
        }

        // [3] 获取用户此考试的考试记录数量 recordCount
        final int recordCount = examMapper.countExamRecordsByUserIdAndExamId(userId, examId);
        final int maxTimes = exam.getMaxTimes();

        // [4] 如果 recordCount >= maxTimes，则不允许创建考试记录
        if (recordCount >= maxTimes) {
            log.warn("[失败] 创建考试记录: 用户 {}, 考试 {}，已经考了 {} 次，最多可以考 {} 次", userId, examId, recordCount, maxTimes);
            return Result.failMessage("考试次数已经用完");
        }

        // [5] 分配试卷
        long paperId = this.assignPaper(userId, exam);
        boolean objective = paperMapper.isPaperObjective(paperId); // 是否客观题试卷

        // [6] 创建考试记录，返回考试记录的 ID
        ExamRecord record = new ExamRecord();
        record.setId(super.nextId()).setUserId(userId).setExamId(examId).setPaperId(paperId).setObjective(objective);
        examMapper.insertExamRecord(record);

        log.info("[成功] 创建考试记录: 用户 {}, 考试 {}, 第 {} 个考试记录 {}，最多可以考 {} 次", userId, examId, recordCount+1, record.getId(), maxTimes);

        return Result.ok(record.getId());
    }

    /**
     * 考试作答
     *
     * @param examRecordAnswer 作答
     * @return 成功创建回答的 payload 为选项的 ID 的数组，否则返回错误信息的 Result
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> answerExamRecord(ExamRecordAnswer examRecordAnswer) {
        // 1. 查询考试记录
        // 2. 如果不能作答则返回
        // 3. 把回答按题目分组
        // 4. 删除题目的所有回答, 然后重新创建回答
        // 5. 更新考试记录的状态:
        //    5.1 submitted 为 true 表示提交试卷，更新考试记录状态为 2 (已提交)，批改客观题
        //    5.2 submitted 为 false 表示普通作答，更新考试记录状态为 1 (已作答)
        // 6. 返回创建了回答的选项 ID 的数组，方便前端从缓冲列表里删除提交成功的记录

        long userId       = examRecordAnswer.getUserId();
        long examRecordId = examRecordAnswer.getExamRecordId();

        if (examRecordAnswer.isSubmitted()) {
            log.info("[开始] 提交考试记录: 用户 {}, 考试记录 {}", userId, examRecordId);
        }

        // [1] 查询考试记录
        ExamRecord examRecord = examMapper.findExamRecordById(examRecordId);

        // [2] 如果不能作答则返回
        Result<Boolean> result = canDoExamination(userId, examRecordId, examRecord);
        if (!result.isSuccess()) {
            return result;
        }

        // [3] 把回答按题目分组
        List<QuestionOptionAnswer> answers = examRecordAnswer.getAnswers();
        Map<Long, List<QuestionOptionAnswer>> answersMap = answers.stream().collect(Collectors.groupingBy(QuestionOptionAnswer::getQuestionId));

        // [4] 删除题目的所有回答, 然后重新创建回答
        answersMap.forEach((questionId, questionAnswers) -> {
            examMapper.deleteQuestionOptionAnswersByExamRecordIdAndQuestionId(examRecordId, questionId);

            for (QuestionOptionAnswer answer : questionAnswers) {
                answer.setExamId(examRecord.getExamId());      // 再次确保考试 ID
                answer.setExamRecordId(examRecordId);          // 再次确保考试记录 ID，避免前端忘了填
                examMapper.insertQuestionOptionAnswer(answer); // 保存到数据库
            }
        });

        // [5] 更新考试记录的状态
        if (examRecordAnswer.isSubmitted()) {
            // [5.1] submitted 为 true 表示提交试卷，更新考试记录状态为 2 (已提交)，批改客观题
            examMapper.updateExamRecordStatus(examRecordId, ExamRecord.STATUS_SUBMITTED);
            log.info("[成功] 提交考试记录: 用户 {}, 考试记录 {}, 提交试卷", userId, examRecordId);

            // 注意: 自动批改客观题 (查找用户作答的考试记录的时候进行了自动批改)
            this.findExamRecord(examRecordId);
        } else {
            examMapper.updateExamRecordStatus(examRecordId, ExamRecord.STATUS_ANSWERED);
        }

        if (examRecordAnswer.isSubmitted()) {
            log.info("[结束] 提交考试记录: 用户 {}, 考试记录 {}", userId, examRecordId);
        }

        // [6] 返回创建了回答的选项 ID 的数组，方便前端从缓冲列表里删除提交成功的记录
        List<Long> optionIds = examRecordAnswer.getAnswers().stream().map(QuestionOptionAnswer::getQuestionOptionId).collect(Collectors.toList());
        return Result.ok(optionIds);
    }

    /**
     * 判断是否可以进行考试作答 (作答是针对考试记录的)
     *
     * @param userId       用户 ID
     * @param examRecordId 考试记录 ID
     * @param examRecord   考试记录
     * @return 可作答返回 Result.ok()，否则返回错误说明
     */
    private Result<Boolean> canDoExamination(long userId, long examRecordId, ExamRecord examRecord) {
        // 1. 如果考试记录为 null，不能作答
        // 2. 考试记录已提交不能作答，不能作答
        // 3. 查找考试记录所属的考试 (考试记录依托于考试，所以理论上考试一定存在)
        // 4. 如果不在考试时间范围内，不能作答
        // 5. 如果考试时间已经用完，不能作答
        // 6. 其他情况均可作答

        // [1] 如果考试记录为 null，不能作答
        if (examRecord == null) {
            log.warn("[失败] 回答考试记录: 用户 {}, 考试记录 {}, 考试记录不存在，不能作答", userId, examRecordId);
            return Result.failMessage("考试记录 " + examRecordId + " 不存在，不能作答");
        }

        // [2] 考试记录已提交不能作答，不能作答
        if (examRecord.getStatus() >= ExamRecord.STATUS_SUBMITTED) {
            log.warn("[失败] 回答考试记录: 用户 {}, 考试记录 {}, 已经提交，不能作答", userId, examRecordId);
            return Result.failMessage("考试记录 " + examRecordId + " 已经提交，不能再作答");
        }

        // [3] 查找考试记录所属的考试 (考试记录依托于考试，所以理论上考试一定存在)
        Exam exam = self.findExam(examRecord.getExamId()); // 从缓存中查询考试

        // [4] 如果不在考试时间范围内，不能作答
        if (exam.getStatus() != Exam.STATUS_STARTED) {
            log.warn("[失败] 回答考试记录: 用户 {}, 考试记录 {}, 不在考试时间范围内，不能作答", userId, examRecordId);
            return Result.failMessage("不在考试时间范围内，不能作答");
        }

        // [5] 如果考试时间已经用完，不能作答 (因为网络传输的延迟等，需要延迟一点时间进行校正，不能非常精确)
        if (examRecord.getElapsedTime() >= exam.getDuration() + SUBMIT_DELAY) {
            log.warn("[失败] 回答考试记录: 用户 {}, 考试记录 {}, 考试时间已经用完，不能作答", userId, examRecordId);
            return Result.failMessage("考试记录 " + examRecordId + " 时间已经用完，不能作答");
        }

        // [6] 其他情况均可作答
        return Result.ok();
    }

    /**
     * 给用户分配考试的试卷
     *
     * @param userId 用户 ID
     * @param exam   考试
     * @return 返回本次考试的试卷 ID
     */
    private long assignPaper(long userId, Exam exam) {
        // 1. 查找出考试的所有试卷 ID、做过的试卷 ID、未做过的试卷 ID
        // 2. 如果还有未做过的试卷，随机从中分配一个
        // 3. 如果所有试卷都做过了，随机从所有的试卷中分配一个

        // [1] 查找出考试的所有试卷 ID、做过的试卷 ID、未做过的试卷 ID
        Set<Long> allPaperIds  = exam.getPaperIdsList(); // 考试所有的 paperId
        Set<Long> usedPaperIds = examMapper.findPaperIdsByUserIdAndExamId(userId, exam.getId()); // 已经使用过的 paperId
        Set<Long> restPaperIds = allPaperIds.stream().filter(id -> !usedPaperIds.contains(id)).collect(Collectors.toSet()); // 未使用过的 paperId

        if (!restPaperIds.isEmpty()) {
            // [2] 如果还有未做过的试卷，随机从中分配一个
            int index = (int)(userId % restPaperIds.size());
            return new ArrayList<>(restPaperIds).get(index);
        } else {
            // [3] 如果所有试卷都做过了，随机从所有的试卷中分配一个
            int index = (int)(userId % allPaperIds.size());
            return new ArrayList<>(allPaperIds).get(index);
        }
    }

    /**
     * 自动批改考试记录里的主观题
     *
     * @param userExamRecord 用户的考试记录
     */
    @Transactional(rollbackFor = Exception.class)
    private void correctObjectiveQuestions(ExamRecord userExamRecord) {
        // 1. 遍历题目，逐题批改
        // 2. 修改考试记录的状态为自动批改

        log.info("[开始] 自动批改客观题: 用户 {}, 考试记录 {}", userExamRecord.getUserId(), userExamRecord.getId());

        // [1] 遍历题目，逐题批改
        for (Question question : userExamRecord.getPaper().getQuestions()) {
            this.correctObjectiveQuestion(userExamRecord.getId(), question);
        }

        // [2] 修改考试记录的状态为自动批改
        userExamRecord.setStatus(ExamRecord.STATUS_AUTO_CORRECTED);
        examMapper.updateExamRecordStatus(userExamRecord.getId(), ExamRecord.STATUS_AUTO_CORRECTED);

        log.info("[结束] 自动批改客观题: 用户 {}, 考试记录 {}", userExamRecord.getUserId(), userExamRecord.getId());
    }

    /**
     * 自动批改主观题
     *
     * @param examRecordId 考试记录 ID
     * @param question     题目
     */
    private void correctObjectiveQuestion(long examRecordId, Question question) {
        // 1. 如果是复合题，递归批改复合题的小题
        // 2. 非客观题则不进行批改
        // 3. 得到所有正确的选项，所有作答的选项
        // 4. 批改客观题 (全对得满分，部分正确得一半分):
        //    4.1 如果作答的选项不为空且是正确的选项的子集
        //        4.1.1 全对: 个数一样
        //        4.1.3 半对: 个数不一样
        //    4.2 错误: 未作答，或者答错任何一个选项
        // 5. 保存题目的作答结果

        // [1] 如果是复合题，递归批改复合题的小题
        if (question.getType() == Question.COMPLEX) {
            for (Question subQuestion : question.getSubQuestions()) {
                correctObjectiveQuestion(examRecordId, subQuestion);
            }
        }

        // [2] 非客观题则不进行批改
        if (question.getType() != Question.SINGLE_CHOICE && question.getType() != Question.MULTIPLE_CHOICE && question.getType() != Question.TFNG) {
            return;
        }

        // [3] 得到所有正确的选项，所有作答的选项
        List<Long> correctOptions = question.getOptions().stream().filter(QuestionOption::isCorrect).map(QuestionOption::getId).collect(Collectors.toList());
        List<Long> checkedOptions = question.getOptions().stream().filter(QuestionOption::isChecked).map(QuestionOption::getId).collect(Collectors.toList());

        // [4] 批改客观题
        QuestionResult result = new QuestionResult(examRecordId, question.getId(), 0D, QuestionResult.STATUS_ERROR); // 默认为错误，得 0 分

        if (!checkedOptions.isEmpty() && correctOptions.containsAll(checkedOptions)) {
            // [4.1] 如果作答选项为空则错误
            if (correctOptions.size() == checkedOptions.size()) {
                // [4.1.1] 全对: 个数一样
                result.setScore(question.getTotalScore()).setStatus(QuestionResult.STATUS_RIGHT);
            } else {
                // [4.1.3] 半对: 个数不一样
                result.setScore(question.getTotalScore() / 2).setStatus(QuestionResult.STATUS_HALF_RIGHT);
            }
        }

        // [5] 保存题目的作答结果
        examMapper.upsertQuestionResult(result);
    }
}
