package com.xtuer.service.exam;

import com.alibaba.fastjson.JSONObject;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.Cached;
import com.xtuer.bean.CacheConst;
import com.xtuer.bean.Result;
import com.xtuer.bean.User;
import com.xtuer.bean.exam.*;
import com.xtuer.dao.ExamDao;
import com.xtuer.mapper.exam.ExamMapper;
import com.xtuer.mapper.exam.PaperMapper;
import com.xtuer.service.BaseService;
import com.xtuer.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 考试服务:
 *     创建或编辑考试: upsertExam
 *     用户的考试信息: findExam(userId, examId) (包含考试记录)
 *     创建考试记录: insertExamRecord(userId, examId)
 *     查找考试记录: findExamRecord(recordId) (考试记录信息、考试的试卷、用户的作答)
 *     考试作答: answerQuestions(questionAnswers)
 *
 * 提示:
 *     1. 考试和试卷与用户无关，数据保存到 MySQL, 考试记录和考试作答和用户有关，保存到 MongoDB
 *     2. 同一个考试，同一个人可以创建多个考试记录，也就是考试允许多次作答
 *     3. 获取用户的某次考试记录时，得到的考试记录里会带上考试的试卷、作答内容，方便前端一次性得到所有数据
 *     4. 自动批改后，把主观题的作答抽取出来保存到 MongoDB exam_question_correct 集合中，用于手动批改，手动批改后一次更新到 MongoDB exam_record 集合
 *
 * 缓存:
 *     1. 使用 ID 查找考试  : ExamService.findExam(examId)
 *     2. 查找指定 ID 的试卷: PaperService.findPaper(paperId)
 *     3. 使用 self 进行调用，说明要走缓存: self.findExam(examId)
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
    private static final int SUBMIT_DELAY = 30; // 考试提交允许延迟的时间, 30 秒 (因为网络传输的延迟等，需要延迟一点时间进行校正，不能非常精确，还有就是学员手动提交点慢了)

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamDao examDao;

    @Autowired
    private PaperService paperService;

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ExamService self;

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
     * 插入或者更新考试
     *
     * @param exam 考试
     * @return 成功操作的 payload 为考试 ID
     */
    @CacheInvalidate(name = CacheConst.CACHE, key = CacheConst.KEY_EXAM)
    public Result<Long> upsertExam(Exam exam) {
        // 1. 分配考试 ID
        // 2. 考试标题不能为空
        // 3. 检查考试时间，有效时间条件为:
        //    3.1 startTime < endTime
        //    3.2 endTime - startTime >= duration
        // 4. 检查最大次数: maxTimes >= 1
        // 5. 试卷必须存在
        // 6. 插入数据库
        // 7. 返回考试 ID

        // [1] 分配考试 ID
        if (Utils.isInvalidId(exam.getId())) {
            exam.setId(super.nextId());
        }

        // [2]、[3]、[4] 验证考试
        Result<Long> validateResult = this.validateExam(exam);
        if (!validateResult.isSuccess()) {
            return validateResult;
        }

        // [5] 试卷必须存在
        Set<Long> paperIds = exam.getPaperIdsList();

        if (paperIds.isEmpty()) {
            return Result.fail("没有试卷");
        }

        for (long paperId : paperIds) {
            if (!paperMapper.paperExists(paperId)) {
                log.warn("[失败] 创建考试: 试卷 {} 不存在", paperId);
                return Result.fail("试卷 " +paperId + " 不存在");
            }
        }

        // [6] 插入数据库
        log.info("[开始] 创建考试: 考试: {}, 标题: {}, 试卷: {}", exam.getId(), exam.getTitle(), exam.getPaperIds());
        examMapper.upsertExam(exam);
        log.info("[结束] 创建考试: 考试 ID {}，标题: {}", exam.getId(), exam.getTitle());

        // [7] 返回考试 ID
        return Result.ok(exam.getId());
    }

    /**
     * 更新考试的基本信息 (名字、开始时间、结束时间、考试时间、最大考试次数)
     *
     * @param exam 考试
     */
    @CacheInvalidate(name = CacheConst.CACHE, key = CacheConst.KEY_EXAM)
    public Result<?> updateExamBaseInfo(Exam exam) {
        // 1. 数据校验
        // 2. 保存到数据库

        // [1] 数据校验
        Result<Long> validateResult = this.validateExam(exam);
        if (!validateResult.isSuccess()) {
            return validateResult;
        }

        // [2] 保存到数据库
        log.info("[开始] 更新试卷信息:{}\n{}", exam.getId(), Utils.toJson(exam));
        examMapper.updateExamBaseInfo(exam);
        log.info("[结束] 更新试卷信息:{}", exam.getId());

        return Result.ok(exam);
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
        exam.setExamRecords(examDao.findExamRecordsByUserIdAndExamId(userId, examId));

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
        // 1. 查找考试、试卷、考试记录
        // 2. 恢复考试状态: 如果考试记录未提交，则查询作答记录
        // 3. 批改客观题与提取主观题作答: 如果考试记录未批改、并且不能继续作答，则自动批改客观题
        // 4. 获取试卷的所有题目和选项
        // 5. 合并题目的作答、得分到试卷的 questions 里，以供用户使用

        // [1] 查找考试、试卷、考试记录
        ExamRecord record = examDao.findExamRecordById(examRecordId);
        Exam  exam  = self.findExam(record.getExamId());
        Paper paper = paperService.findPaper(record.getPaperId());
        record.setExam(exam);
        record.setPaper(paper);

        // [2] 恢复考试状态: 如果考试记录未提交，则查询作答记录
        if (record.getStatus() < ExamRecord.STATUS_SUBMITTED) {
            List<QuestionWithAnswer> qas = examDao.findQuestionsWithAnswerByExamRecordId(record.getId());
            record.setQuestions(qas); // record 中的 questions 为题目的作答和得分
        }

        // [3] 批改客观题与提取主观题作答: 如果考试记录未批改、并且不能继续作答，则自动批改客观题
        if (record.getStatus() < ExamRecord.STATUS_CORRECTED_AUTO && !this.canDoExamination(record.getUserId(), record.getId(), record).isSuccess()) {
            this.correctObjectiveQuestions(record, paper);
            this.extractSubjectiveQuestionsForAnswer(record, paper); // 提取主观题作答，以便逐题批改
        }

        // [4] 获取试卷的所有题目和选项
        Map<Long, Question>     questions = paperService.getAllQuestionsOfPaper(paper);
        Map<Long, QuestionOption> options = paperService.getAllQuestionOptionsOfPaper(paper);

        // [5] 合并题目的作答、得分到试卷的 questions 里，以供前端直接使用
        //     qa: 题目 (带有作答信息)
        //     question: 试卷中的题目，把作答信息合并到它里面
        record.getQuestions().forEach(qa -> {
            Question question = questions.get(qa.getQuestionId());
            question.setScore(qa.getScore());
            question.setScoreStatus(qa.getScoreStatus());

            qa.getAnswers().forEach(oa -> {
                QuestionOption option = options.get(oa.getQuestionOptionId());
                option.setChecked(true);
                option.setValue(oa.getValue());
                option.setAnswer(oa.getContent());
            });
        });

        return record;
    }

    /**
     * 查找指定考试的所有考试记录
     *
     * @param examId 考试 ID
     * @return 返回查找到的考试记录数组
     */
    public List<ExamRecord> findExamRecordsByExamId(long examId) {
        return examDao.findExamRecordsByExamId(examId);
    }

    /**
     * 查询指定用户的多个考试的考试记录
     *
     * @param userId  用户 ID
     * @param examIds 考试 ID 的数组
     * @return 返回考试记录的 Map，key 为考试 ID
     */
    public Map<Long, List<ExamRecord>> findExamRecordsByExamIds(long userId, List<Long> examIds) {
        if (examIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, List<ExamRecord>> records = examDao.findExamRecordsByExamIds(userId, examIds);
        return records;
    }

    /**
     * 创建用户某次考试的考试记录
     *
     * @param user   用户
     * @param examId 考试 ID
     * @return 成功创建考试记录时 payload 为考试记录 ID，否则返回错误的 Result 对象
     */
    public Result<Long> insertExamRecord(User user, long examId) {
        // 1. 查找考试
        // 2. 检查考试状态，只有考试中时才允许创建考试记录
        // 3. 获取用户此考试的考试记录数量 recordCount
        // 4. 如果 recordCount >= maxTimes，则不允许创建考试记录
        // 5. 分配试卷
        // 6. 创建考试记录，返回考试记录的 ID

        // [1] 查找考试
        Exam exam = self.findExam(examId);
        long userId = user.getId();

        if (exam == null) {
            log.warn("[失败] 创建考试记录: 用户 {}, 考试 {}，考试不存在", userId, examId);
            return Result.fail("考试不存在: " + examId);
        }

        // [2] 检查考试状态，只有考试中时才允许创建考试记录
        if (exam.getStatus() == Exam.STATUS_NOT_STARTED) {
            log.warn("[失败] 创建考试记录: 用户 {}, 考试 {}，考试未开始", userId, examId);
            return Result.fail("考试未开始");
        }
        if (exam.getStatus() == Exam.STATUS_ENDED) {
            log.warn("[失败] 创建考试记录: 用户 {}, 考试 {}，考试已结束", userId, examId);
            return Result.fail("考试已结束");
        }

        // [3] 获取用户此考试的考试记录数量 recordCount
        final int recordCount = examDao.countExamRecordsByUserIdAndExamId(userId, examId);
        final int maxTimes = exam.getMaxTimes();

        // [4] 如果 recordCount >= maxTimes，则不允许创建考试记录
        if (recordCount >= maxTimes) {
            log.warn("[失败] 创建考试记录: 用户 {}, 考试 {}，已经考了 {} 次，最多可以考 {} 次", userId, examId, recordCount, maxTimes);
            return Result.fail("考试次数已经用完");
        }

        // [5] 分配试卷
        long paperId = this.assignPaper(userId, exam);
        Paper paper = paperService.findPaper(paperId);

        // [6] 创建考试记录，返回考试记录的 ID
        ExamRecord record = new ExamRecord();
        record.setId(super.nextId())
                .setUserId(userId)
                .setUsername(user.getUsername())
                .setNickname(user.getNickname())
                .setExamId(examId)
                .setPaperId(paper.getId())
                .setObjective(paper.isObjective())
                .setTotalScore(paper.getTotalScore())
                .setStartAt(new Date())
                .setTickAt(new Date());
        examDao.upsertExamRecord(record);

        log.info("[成功] 创建考试记录: 用户 {}, 考试 {}, 第 {} 个考试记录 {}，最多可以考 {} 次", userId, examId, recordCount+1, record.getId(), maxTimes);

        return Result.ok(record.getId());
    }

    /**
     * 对考试记录进行作答:
     *     A. 如果 submitted 为 false 则对单题进行作答
     *     B. 如果 submitted 为 true 则提交试卷，对所有题目进行作答
     *
     * @param questionAnswers 作答
     * @return 成功创建回答的 payload 为选项的 ID，否则返回错误信息的 Result
     */
    public Result<?> answerQuestions(QuestionAnswers questionAnswers) {
        // 1. 查询考试记录
        // 2. 如果不能作答则返回
        // 3. 设置选项作答的题目 ID
        // 4. 如果只是作答单个题目，保存作答记录并返回
        // 5. 如果是提交试卷:
        //    5.1 考试记录中只保留主观题和客观题的作答 (过滤掉题型题、复合题大题的空白作答)
        //    5.2 保存所有题目的作答到考试记录
        //    5.3 修改考试记录的状态为已提交
        //    5.4 保存所有题目的作答到考试记录
        //    5.5 保存考试记录
        //    5.6 自动批改客观题
        //    5.7 提取考试记录中主观题作答，以便逐题批改

        // [1] 查询考试记录
        long userId   = questionAnswers.getUserId();
        long recordId = questionAnswers.getExamRecordId();
        ExamRecord record = examDao.findExamRecordById(recordId);

        // [2] 如果不能作答则返回
        Result<?> test = this.canDoExamination(userId, recordId, record);
        if (!test.isSuccess()) {
            return test;
        }

        // [3] 设置选项作答的题目 ID
        questionAnswers.getQuestions().forEach(question -> {
            question.getAnswers().forEach(answer -> {
                answer.setQuestionId(question.getQuestionId());
            });
        });

        // [4] 如果只是作答单个题目，保存作答记录并返回
        //     (因为调查问卷不进行单题保存，为了后期统计，提交时所有作答都保存到作答记录表里)
        questionAnswers.getQuestions().forEach(question -> {
            question.setExamId(record.getExamId());
            question.setExamRecordId(recordId);
            examDao.upsertQuestionAnswers(question);
        });

        if (!questionAnswers.isSubmitted()) {
            // QuestionWithAnswer question = questionAnswers.getQuestions().get(0);
            // question.setExamId(record.getExamId());
            // question.setExamRecordId(recordId);
            // examDao.upsertQuestionAnswers(question);

            return Result.ok(questionAnswers.getQuestions().get(0).getQuestionId());
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////
        //                                          提交试卷                                             //
        //////////////////////////////////////////////////////////////////////////////////////////////////
        log.info("[开始] 提交考试记录: 用户 {}, 考试记录 {}", userId, recordId);

        // [5.1] 考试记录中只保留主观题和客观题的作答 (过滤掉题型题、复合题大题的空白作答)
        Paper paper = paperService.findPaper(record.getPaperId());
        Map<Long, Question> questions = paperService.getAllQuestionsOfPaper(paper);

        List<QuestionWithAnswer> qas = questionAnswers.getQuestions().stream().filter(qa -> {
            Question q = questions.get(qa.getQuestionId());
            qa.setQuestionType(q.getType()); // 设置题目类型
            return q.isObjective() || q.isSubjective();
        }).collect(Collectors.toList());
        // [5.2] 保存所有题目的作答到考试记录
        record.setQuestions(qas);

        // [5.3] 修改考试记录的状态为已提交
        record.setStatus(ExamRecord.STATUS_SUBMITTED);
        record.setSubmittedAt(questionAnswers.getCreatedAt());

        // [5.4] 保存考试记录
        examDao.upsertExamRecord(record);

        // [5.5] 自动批改客观题
        this.correctObjectiveQuestions(record, paper);

        // [5.6] 提取考试记录中主观题作答，以便逐题批改
        this.extractSubjectiveQuestionsForAnswer(record, paper);

        log.info("[结束] 提交考试记录: 用户 {}, 考试记录 {}", userId, recordId);
        return Result.ok("", "交卷成功");
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
            return Result.fail("考试记录 " + examRecordId + " 不存在，不能作答");
        }

        // [2] 考试记录已提交不能作答，不能作答
        if (examRecord.getStatus() >= ExamRecord.STATUS_SUBMITTED) {
            log.warn("[失败] 回答考试记录: 用户 {}, 考试记录 {}, 已经提交，不能作答", userId, examRecordId);
            return Result.fail("考试记录 " + examRecordId + " 已经提交，不能再作答");
        }

        // [3] 查找考试记录所属的考试 (考试记录依托于考试，所以理论上考试一定存在)
        Exam exam = self.findExam(examRecord.getExamId()); // 从缓存中查询考试

        // [4] 如果不在考试时间范围内，不能作答
        if (exam.getStatus() != Exam.STATUS_STARTED) {
            log.warn("[失败] 回答考试记录: 用户 {}, 考试记录 {}, 不在考试时间范围内，不能作答", userId, examRecordId);
            return Result.fail("不在考试时间范围内，不能作答");
        }

        // [5] 如果考试时间已经用完，不能作答 (因为网络传输的延迟等，需要延迟一点时间进行校正，不能非常精确)
        if (examRecord.getElapsedTime() >= exam.getDuration() + SUBMIT_DELAY) {
            log.warn("[失败] 回答考试记录: 用户 {}, 考试记录 {}, 考试时间已经用完，不能作答", userId, examRecordId);
            return Result.fail("考试记录 " + examRecordId + " 时间已经用完，不能作答");
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
        Set<Long> usedPaperIds = examDao.findPaperIdsByUserIdAndExamId(userId, exam.getId()); // 已经使用过的 paperId
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
     * 自动批改客观题: 试卷中有题目的满分和正确选项信息，考试记录中题目的作答根据题目的信息进行判断
     *
     * @param record 考试记录
     * @param paper  试卷
     */
    private void correctObjectiveQuestions(ExamRecord record, Paper paper) {
        // 1. 得到试卷里所有的题目
        // 2. 逐个批改考试记录里作答的客观题
        // 3. 得到正批改的题目的所有正确的选项，所有作答的选项
        // 4. 批改客观题 (全对得满分，部分正确得一半分):
        //    4.1 如果作答的选项不为空且是正确的选项的子集
        //        4.1.1 全对: 个数一样
        //        4.1.3 半对: 个数不一样
        //    4.2 错误: 未作答，或者答错任何一个选项
        // 5. 计算考试得分
        // 6. 修改考试记录的状态: 客观题试卷为批改结束，主观题试卷为自动批改
        // 7. 保存考试记录到数据库

        log.info("[开始] 自动批改客观题: 用户 {}, 考试记录 {}", record.getUserId(), record.getId());

        // [1] 得到试卷里所有的题目
        Map<Long, Question> questions = paperService.getAllQuestionsOfPaper(paper);

        // [2] 逐个批改考试记录里作答的客观题
        record.getQuestions().forEach(qwa -> {
            Question question = questions.get(qwa.getQuestionId());

            // 非客观题则不进行批改
            if (question == null || !question.isObjective()) {
                return;
            }

            // [3] 得到正批改的题目的所有正确的选项，所有作答的选项
            List<Long> correctOptions = question.getOptions().stream().filter(QuestionOption::isCorrect).map(QuestionOption::getId).collect(Collectors.toList());
            List<Long> checkedOptions = qwa.getAnswers().stream().map(QuestionOptionAnswer::getQuestionOptionId).collect(Collectors.toList());

            // [4] 批改客观题 (全对得满分，部分正确得一半分)
            if (!checkedOptions.isEmpty() && correctOptions.containsAll(checkedOptions)) {
                // [4.1] 如果作答的选项不为空且是正确的选项的子集

                if (correctOptions.size() == checkedOptions.size()) {
                    // [4.1.1] 全对: 个数一样
                    qwa.setScore(question.getTotalScore()).setScoreStatus(Question.SCORE_STATUS_RIGHT);
                } else {
                    // [4.1.2] 半对: 个数不一样
                    qwa.setScore(question.getTotalScore() / 2).setScoreStatus(Question.SCORE_STATUS_HALF_RIGHT);
                }
            } else {
                // [4.2] 错误: 未作答，或者答错任何一个选项
                qwa.setScore(0).setScoreStatus(Question.SCORE_STATUS_ERROR);
            }
        });

        // [5] 计算考试得分
        this.scoringExamRecord(record);

        // [6] 修改考试记录的状态: 客观题试卷为批改结束，主观题试卷为自动批改
        record.setObjective(paper.isObjective());
        record.setStatus(paper.isObjective() ? ExamRecord.STATUS_CORRECTED_FINISH : ExamRecord.STATUS_CORRECTED_AUTO);

        // [7] 保存考试记录到数据库
        examDao.upsertExamRecord(record);

        log.info("[结束] 自动批改客观题: 用户 {}, 考试记录 {}", record.getUserId(), record.getId());
    }

    /**
     * 批改主观题的作答
     *
     * @param teacherId 批改题目的老师的 ID
     * @param questionCorrects 题目批改的数组
     */
    public Result<Boolean> correctSubjectiveQuestions(long teacherId, List<QuestionCorrect> questionCorrects) {
        // 1. 查询考试记录
        // 2. 获取试卷中的所有主观题 (主要用于获取题目的满分和类型)
        // 3. 查询此考试记录的所有主观题作答，逐题批改
        // 4. 保存批改过的主观题作答
        // 5. 如果只是批改了部分主观题的作答 (还有 scoreStatus 等于 0)，设置考试记录状态为手动批改
        // 6. 如果已经批改了所有主观题的作答 (所有 scoreStatus 不为 0)，
        //    6.1 把所有主观题的作答保存到考试记录
        //    6.2 计算考试得分
        //    6.3 标记考试记录批改完成
        // 7. 保存考试记录

        if (questionCorrects.isEmpty()) {
            return Result.ok();
        }

        long recordId = questionCorrects.get(0).getExamRecordId();

        log.info("[开始] 考试批改: 考试记录: {}, 老师: {}", recordId, teacherId);

        // [1] 查询考试记录
        ExamRecord record = self.findExamRecord(recordId);

        // [2] 获取试卷中的所有主观题 (主要用于获取题目的满分和类型)
        Map<Long, Question> subjectiveQuestions = paperService.getSubjectiveQuestionsOfPaper(record.getPaper())
                .stream().collect(Collectors.toMap(Question::getId, q -> q, (o, n) -> n));

        // [3] 查询此考试记录的所有主观题作答，逐题批改
        List<QuestionWithAnswer> questionsWithAnswer = examDao.findSubjectiveQuestionsWithAnswerByExamRecordId(recordId);

        for (QuestionCorrect correct : questionCorrects) {
            for (QuestionWithAnswer answer : questionsWithAnswer) {
                // 找到批改对应的主观题作答
                if (answer.getQuestionId() == correct.getQuestionId()) {
                    // A. 得分与满分
                    Question question = subjectiveQuestions.get(correct.getQuestionId());
                    double score = correct.getScore();
                    double totalScore = question.getTotalScore();

                    // B. 分数校验
                    if (score < 0) {
                        return Result.fail("得分不能为负");
                    }
                    if (score > totalScore) {
                        return Result.fail("得分不能超过题目的满分 " + totalScore);
                    }

                    // C. 计算得分状态: 0 (未知)、1 (错误)、2 (半对)、3 (全对)
                    int scoreStatus = 0;
                    if (score == 0) {
                        scoreStatus = 1;
                    } else if (0 < score && score < totalScore) {
                        scoreStatus = 2;
                    } else if (score == totalScore) {
                        scoreStatus = 3;
                    }

                    // D. 设置作答的批改信息
                    answer.setTeacherId(teacherId)
                            .setScore(score)
                            .setScoreStatus(scoreStatus)
                            .setQuestionType(question.getType());
                }
            }
        }

        // [4] 保存批改过的主观题作答
        examDao.upsertSubjectiveQuestionsWithAnswer(questionsWithAnswer);

        // [5] 如果只是批改了部分主观题的作答 (还有 scoreStatus 等于 0)，设置考试记录状态为手动批改
        boolean unfinished = questionsWithAnswer.stream().anyMatch(q -> q.getScoreStatus() == 0);

        if (unfinished) {
            record.setStatus(ExamRecord.STATUS_CORRECTED_MANUAL);
            log.info("[信息] 考试批改: 考试记录: {}, 手动批改", recordId);
        } else {
            // [6] 如果已经批改了所有主观题的作答 (所有 scoreStatus 不为 0)
            for (QuestionWithAnswer qa : questionsWithAnswer) {
                for (QuestionWithAnswer qaInRecord : record.getQuestions()) {
                    // [6.1] 把所有主观题的作答保存到考试记录
                    if (qa.getQuestionId() == qaInRecord.getQuestionId()) {
                        qaInRecord.setTeacherId(qa.getTeacherId())
                                .setScore(qa.getScore())
                                .setScoreStatus(qa.getScoreStatus())
                                .setQuestionType(qa.getQuestionType());
                    }
                }
            }

            // [6.2] 计算考试得分
            scoringExamRecord(record);

            // [6.3] 标记考试记录批改完成
            record.setStatus(ExamRecord.STATUS_CORRECTED_FINISH);
            log.info("[信息] 考试批改: 考试记录: {}, 完成批改", recordId);
        }

        // [7] 保存考试记录
        examDao.upsertExamRecord(record);

        log.info("[结束] 考试批改: 考试记录: {}, 老师: {}", recordId, teacherId);
        return Result.ok();
    }

    /**
     * 提取考试记录中主观题作答，保存到到主观题批改表中
     *
     * @param record 考试记录
     * @param paper  试卷
     */
    private void extractSubjectiveQuestionsForAnswer(ExamRecord record, Paper paper) {
        // 提示: 复合题小题的作答都展开放到复合题的 answers 中，批改时按整个复合题进行批改
        // 1. 获取考试记录中所有题目选项的作答 optionAnswers
        // 2. 遍历试卷中的主观题，逐个处理
        // 3. 获取主观题的作答，从 optionAnswers 中获取
        // 4. 主观题没有作答，则不继续处理
        // 5. 保存主观题的作答

        // 作答的题目
        List<QuestionWithAnswer> questions = new LinkedList<>();

        // [1] 获取考试记录中所有题目选项的作答 optionAnswers
        Map<Long, QuestionOptionAnswer> optionAnswers = record.getQuestions().stream()
                .map(QuestionWithAnswer::getAnswers)
                .flatMap(List::stream)
                .collect(Collectors.toMap(QuestionOptionAnswer::getQuestionOptionId, o -> o, (o, n) -> n));

        // [2] 遍历试卷中的主观题，逐个处理
        paperService.getSubjectiveQuestionsOfPaper(paper).forEach(subjectiveQuestion -> {
            QuestionWithAnswer question = new QuestionWithAnswer();

            // [3] 获取主观题的作答，从 optionAnswers 中获取
            questionService.getQuestionOptions(subjectiveQuestion).forEach(option -> {
                QuestionOptionAnswer answer = optionAnswers.get(option.getId());

                if (answer != null) {
                    question.getAnswers().add(answer);
                }
            });

            // [4] 主观题没有作答，则不继续处理
            if (question.getAnswers().size() == 0) {
                log.warn("[提取] 主观题作答: 用户 {}, 考试记录 {}, 主观题 {}，没有作答", record.getUserId(), record.getId(), subjectiveQuestion.getId());
                return;
            }

            question.setExamId(record.getExamId())
                    .setExamRecordId(record.getId())
                    .setQuestionId(subjectiveQuestion.getId());
            questions.add(question);
        });

        // [5] 保存主观题的作答
        examDao.upsertSubjectiveQuestionsWithAnswer(questions);
    }

    /**
     * 计算考试得分: 考试得分、客观题得分、主观题得分
     *
     * @param record 考试记录
     */
    private void scoringExamRecord(ExamRecord record) {
        double objectiveSum  = 0; // 客观题得分
        double subjectiveSum = 0; // 主观题得分

        for (QuestionWithAnswer qa : record.getQuestions()) {
            if (questionService.isObjectiveQuestionType(qa.getQuestionType())) {
                objectiveSum += qa.getScore();
            } else if (questionService.isSubjectiveQuestionType(qa.getQuestionType())) {
                subjectiveSum += qa.getScore();
            }
        }

        record.setScore(objectiveSum + subjectiveSum);
        record.setObjectiveScore(objectiveSum);
        record.setSubjectiveScore(subjectiveSum);
    }

    /**
     * 获取考试的所有主观题
     *
     * @param examId 考试 ID
     * @return 返回主观题的集合
     */
    public Set<Question> getSubjectiveQuestionsOfExam(long examId) {
        // 1. 查询考试
        // 2. 获取考试的所有试卷 ID
        // 3. 遍历每一个试卷，得到试卷的主观题

        // [1] 查询考试
        // [2] 获取考试的所有试卷 ID
        Exam exam = self.findExam(examId);
        Set<Long> paperIds  = exam.getPaperIdsList();
        Set<Question> questions = new TreeSet<>((a, b) -> (int) (a.getId() - b.getId()));

        // [3] 遍历每一个试卷，得到试卷的主观题
        for (long paperId : paperIds) {
            Paper paper = paperService.findPaper(paperId);
            questions.addAll(paperService.getSubjectiveQuestionsOfPaper(paper));
        }

        return questions;
    }

    /**
     * 验证考试
     *
     * @param exam 考试
     */
    private Result<Long> validateExam(Exam exam) {
        // 1. 考试标题不能为空
        // 2. 检查考试时间，有效时间条件为:
        //    2.1 startTime < endTime
        //    2.2 endTime - startTime >= duration
        // 3. 检查最大次数: maxTimes >= 1

        // [1] 考试标题不能为空
        if (StringUtils.isBlank(exam.getTitle())) {
            return Result.fail("考试的标题不能为空");
        }

        // [2] 检查考试时间，有效时间条件为:
        //    [2.1] startTime < endTime
        //    [2.2] endTime - startTime >= duration
        if (exam.getStartTime().after(exam.getEndTime())) {
            return Result.fail("考试开始时间必须小于考试结束时间");
        }
        if ((exam.getEndTime().getTime() - exam.getStartTime().getTime()) / 1000 < exam.getDuration()) {
            return Result.fail("考试时间区间必须大于考试持续时间");
        }

        // [3] 检查最大次数: maxTimes >= 1
        if (exam.getMaxTimes() < 1) {
            return Result.fail("最大考试次数必须大于等于 1");
        }

        return Result.ok();
    }

    /**
     * 统计某次考试的作答情况:
     *     A. 考试记录数 (只允许一次的作答即为作答人数，例如调查问卷)
     *     B. 单选题、多选题、判断题每个选项的选择次数
     *     C. 星级题每个级别的打星次数
     *
     * 返回结果如:
     * {
     *     "examRecordsCount": 2, // 此考试的考试记录数
     *     "questionOptionAnswerCounts": {
     *         "421867174458560514": 1, // key 为选项 ID, value 为此选项的作答次数
     *         "443284708759965697": 2,
     *     },
     *     "questionOptionStarCounts": {
     *         "443284708759965697": { // key 为选项 ID
     *             "3": 1, // key 为星级值, value 为选此星级的数量
     *             "4": 5,
     *         }
     *     }
     * }
     *
     * @param examId 考试 ID
     * @return 返回考试记录数的统计
     */
    public JSONObject statisticizeExamRecords(long examId) {
        // 1. 查询考试记录数
        // 2. 查询单选题、多选题、判断题每个选项的选择次数 (这里查询出了所有的，前端根据试卷里的选项进行过滤)
        // 3. 查询星级题每个级别的打星次数
        //    3.1 按选项分组
        //    3.2 按星级统计

        JSONObject statistics = new JSONObject();

        // [1] 查询考试记录数
        int recordsCount = examDao.countExamRecords(examId);
        statistics.put("examRecordsCount", recordsCount);

        // [2] 查询单选题、多选题、判断题每个选项的选择次数
        Map<Long, Integer> optionAnswersCount = examDao.countExamQuestionOptionAnswers(examId);
        statistics.put("questionOptionAnswerCounts", optionAnswersCount);

        // [3] 查询星级题每个级别的打星次数
        Map<Long, Map<String, Integer>> questionOptionStarCounts = examDao.countExamQuestionOptionStars(examId);
        statistics.put("questionOptionStarCounts", questionOptionStarCounts);

        return statistics;
    }

    /**
     * 统计考试的考试记录数量
     *
     * @param examIds 考试 ID 的数组
     * @return 返回考试及它的考试记录数量的数组
     */
    public List<ExamRecordCount> countExamRecordsOfExams(List<Long> examIds) {
        return examDao.countExamRecordsOfExams(examIds);
    }

    /**
     * 查询指定考试的问答题的非空作答内容
     *
     * @param examId     考试 ID
     * @param questionId 题目 ID
     * @return 返回作答内容的数组
     */
    public List<String> findEssayQuestionAnswers(long examId, long questionId) {
        return examDao.findEssayQuestionAnswers(examId, questionId);
    }
}
