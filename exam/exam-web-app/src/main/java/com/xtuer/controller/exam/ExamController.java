package com.xtuer.controller.exam;

import com.alibaba.fastjson.JSONObject;
import com.xtuer.bean.Result;
import com.xtuer.bean.User;
import com.xtuer.bean.exam.*;
import com.xtuer.config.AppConfig;
import com.xtuer.controller.BaseController;
import com.xtuer.bean.Urls;
import com.xtuer.dao.ExamDao;
import com.xtuer.mapper.exam.ExamMapper;
import com.xtuer.service.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 考试的控制器
 */
@RestController
public class ExamController extends BaseController {
    @Autowired
    private ExamService examService;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamDao examDao;

    @Autowired
    private AppConfig config;

    /**
     * 查询指定 ID 的考试
     *
     * 网址: http://localhost:8080/api/exam/exams/{examId}
     * 参数: 无
     *
     * @return 成功的 payload 为考试
     */
    @GetMapping(Urls.API_EXAMS_BY_ID)
    public Result<Exam> findExamById(@PathVariable long examId) {
        Exam exam = examService.findExam(examId);
        return Result.single(exam, "考试不存在: " + examId);
    }

    /**
     * 查找当前机构的考试
     *
     * 网址: http://localhost:8080/api/exam/exams/ofCurrentOrg
     * 参数: 无
     *
     * @return 成功的 payload 为考试的数组
     */
    @GetMapping(Urls.API_EXAMS_OF_CURRENT_ORG)
    public Result<List<Exam>> findExamsOfCurrentOrg() {
        long orgId = super.getCurrentOrganizationId();
        return Result.ok(examMapper.findExamsByOrgId(orgId));
    }

    /**
     * 创建或者更新考试
     *
     * 网址: http://localhost:8080/api/exam/exams/ofCurrentOrg
     * 参数:
     *      title     [必选]: 考试标题
     *      paperId   [必选]: 试卷 ID
     *      startTime [必选]: 考试开始时间
     *      endTime   [必选]: 考试结束时间
     *      duration  [必选]: 考试时长
     *      maxTimes  [必选]: 最多允许考试次数
     *
     * @param exam 考试对象
     * @return 成功的 payload 为考试的 ID
     */
    @PutMapping(Urls.API_EXAMS_OF_CURRENT_ORG)
    public Result<Long> upsertExam(Exam exam) {
        long orgId = super.getCurrentOrganizationId();
        exam.setHolderId(orgId);

        return examService.upsertExam(exam);
    }

    /**
     * 更新考试的基本信息 (名字、开始时间、结束时间、考试时间、最大考试次数)
     *
     * 网址: http://localhost:8080/api/exam/exams/{examId}
     * 参数:
     *      id       : 考试 ID
     *      title    : 考试名字
     *      startTime: 开始时间
     *      endTime  : 结束时间
     *      duration : 考试时长
     *      maxTimes : 最多考试次数
     *
     * @param exam 考试
     * @return payload 为考试
     */
    @PutMapping(Urls.API_EXAMS_BY_ID)
    public Result<?> updateExamBaseInfo(Exam exam) {
        return examService.updateExamBaseInfo(exam);
    }

    /**
     * 查找用户的考试信息，如果用户在此考试中进行过作答，同时查找出所有相关的考试记录
     *
     * 网址: http://localhost:8080/api/exam/users/{userId}/exams/{examId}
     * 参数: 无
     *
     * @param userId 用户 ID
     * @param examId 考试 ID
     * @return 成功的 payload 为用户的考试对象
     */
    @GetMapping(Urls.API_USER_EXAMS)
    public Result<Exam> findExam(@PathVariable long userId, @PathVariable long examId) {
        Exam exam = examService.findExam(userId, examId);

        if (exam != null) {
            return Result.ok(exam);
        } else {
            return Result.fail("找不到考试 " + examId);
        }
    }

    /**
     * 创建用户某次考试的考试记录
     *
     * 网址: http://localhost:8080/api/exam/users/{userId}/exams/{examId}/records
     * 参数: 无
     *
     * @param userId 用户 ID
     * @param examId 考试 ID
     * @return 成功的 payload 为考试记录 ID
     */
    @PostMapping(Urls.API_USER_EXAM_RECORDS)
    public Result<Long> insertExamRecord(@PathVariable long userId, @PathVariable long examId) {
        User user = super.getCurrentUser();
        return examService.insertExamRecord(user, examId);
    }

    /**
     * 查询用户的考试记录，内容有: 考试记录信息、考试的试卷、用户的作答
     *
     * 网址: http://localhost:8080/api/exam/users/{userId}/exams/{examId}/records/{recordId}
     * 参数: 无
     *
     * @param recordId 考试记录 ID
     * @return 成功的 payload 为用户的考试记录
     */
    @GetMapping(Urls.API_USER_EXAM_RECORDS_BY_ID)
    public Result<ExamRecord> findExamRecord(@PathVariable long recordId) {
        ExamRecord record = examService.findExamRecord(recordId);

        if (record != null) {
            return Result.ok(record);
        } else {
            return Result.fail("找不到考试记录 " + recordId);
        }
    }

    /**
     * 查找指定考试的所有考试记录
     *
     * 网址: http://localhost:8080/api/exam/exams/{examId}/examRecords
     * 参数: 无
     *
     * @param examId 考试 ID
     * @return payload 为考试记录的数组
     */
    @GetMapping(Urls.API_EXAM_RECORDS_BY_EXAM_ID)
    public Result<List<ExamRecord>> findExamRecordsByExamId(@PathVariable long examId) {
        List<ExamRecord> records = examService.findExamRecordsByExamId(examId);
        return Result.ok(records);
    }

    /**
     * 查询指定用户的多个考试的考试记录
     *
     * 网址: http://localhost:8080/api/exam/users/{userId}/exams/records
     * 参数: examIds: 考试 ID 的数组
     *
     * @param userId  用户 ID
     * @param examIds 考试 ID 的数组
     * @return payload 为考试记录的 Map，key 为考试 ID
     */
    @GetMapping(Urls.API_EXAM_RECORDS_BY_EXAM_IDS)
    public Result<Map<Long, List<ExamRecord>>> findExamRecordsByExamIds(@PathVariable long userId, @RequestParam(name = "examIds") List<Long> examIds) {
        Map<Long, List<ExamRecord>> records = examService.findExamRecordsByExamIds(userId, examIds);
        return Result.ok(records);
    }

    /**
     * 对考试的题目进行作答，只有 2 种情况:
     * 1. 对单个题目进行作答: submitted 为 false, questions 数组中只有一个元素 (指定题目的作答)
     * 2. 提交整个试卷的作答: submitted 为 true, questions 数组中有整个试卷所有题目的作答
     *
     * 网址: http://localhost:8080/api/exam/users/{userId}/exams/{examId}/records/{recordId}/answer
     * 参数: 无
     * Request Body 为:
     * {
     *     "submitted": false,
     *     "questions": [
     *          { "questionId": 0, answers: [{ "questionOptionId": 0, "content": "xxx", "value": 1 }, { "questionOptionId": 0, "content": "xxx", "value": 1 }] },
     *          { "questionId": 1, answers: [{ "questionOptionId": 0, "content": "xxx", "value": 2 }, { "questionOptionId": 0, "content": "xxx", "value": 2 }] },
     *          { "questionId": 2, answers: [{ "questionOptionId": 0, "content": "xxx", "value": 0 }, { "questionOptionId": 0, "content": "xxx", "value": 0 }] },
     *     ]
     * }
     *
     * @param questionAnswers 题目的作答
     * @return 1. 如果是提交试卷，则 code 为 1，否则 code 为 0
     *         2. 如果是单题作答，则 code 为 0，payload 为题目 ID，方便前端从作答队列中删除此题目 (如果实现了的话)
     */
    @PostMapping(Urls.API_USER_EXAM_ANSWER_QUESTIONS)
    public Result<Long> answerQuestions(@PathVariable long recordId, @RequestBody QuestionAnswers questionAnswers) {
        // 1. 设置考试记录的信息
        // 2. 提交考试记录
        //    2.1 使用 MQ 时写入到 MQ
        //    2.2 不用 MQ 时写入到 DB
        // 3. 立即返回

        // [1] 设置考试记录的信息
        // userId 和 examRecordId 在登录信息和 URL 中可以获得
        questionAnswers.setUserId(super.getCurrentUserId());
        questionAnswers.setExamRecordId(recordId);

        // [2] 提交考试记录
        //    [2.1] 使用 MQ 时写入到 MQ
        //    [2.2] 不用 MQ 时写入到 DB
        // if (config.isMqEnabled()) {
        //     // 提交试卷时发送同步消息，题目作答时发送异步消息
        //     if (questionAnswers.isSubmitted()) {
        //         messageProducer.sendAnswerQuestionsMessage(questionAnswers);
        //     } else {
        //         messageProducer.sendAnswerQuestionsMessageAsync(questionAnswers);
        //     }
        // } else {
        //     examService.answerQuestions(questionAnswers); // 直接写到数据库
        // }

        examService.answerQuestions(questionAnswers); // 直接写到数据库

        // [3] 立即返回
        if (questionAnswers.isSubmitted()) {
            // 提交试卷
            return new Result<>(true, "试卷提交成功", null, 1);
        } else {
            // 只对一个题进行作答
            long questionId = questionAnswers.getQuestions().get(0).getQuestionId();
            return Result.ok(questionId);
        }
    }

    /**
     * 批改主观题
     *
     * 网址: http://localhost:8080/api/exam/exams/{examId}/records/{recordId}/correct
     * 参数: 无
     * 请求体: [{ examRecordId, questionId, score, comment }]
     *
     * @param questionCorrects 主观题的批改内容
     */
    @PutMapping(Urls.API_EXAM_CORRECT_QUESTIONS)
    public Result<Boolean> correctSubjectiveQuestions(@RequestBody List<QuestionCorrect> questionCorrects) {
        long teacherId = super.getCurrentUserId();
        return examService.correctSubjectiveQuestions(teacherId, questionCorrects);
    }

    /**
     * 统计考试的作答情况
     *
     * 网址: http://localhost:8080/api/exam/exams/{examId}/answers/statistics
     * 参数: 无
     *
     * @param examId 考试 ID
     * @return payload 为作答的统计
     */
    @GetMapping(Urls.API_EXAM_ANSWER_STATISTICS)
    public Result<JSONObject> statisticizeExamRecords(@PathVariable long examId) {
        return Result.ok(examService.statisticizeExamRecords(examId));
    }

    /**
     * 统计考试的考试记录数量
     *
     * 网址: http://localhost:8080/api/exam/examRecords/count
     * 参数: examIds: 考试 ID 的数组
     *
     * @param examIds 考试 ID 的数组
     * @return payload 为考试及其考试记录数量对象的数组
     */
    @GetMapping(Urls.API_EXAM_RECORDS_COUNT)
    public Result<List<ExamRecordCount>> countExamRecordsOfExams(@RequestParam List<Long> examIds) {
        return Result.ok(examService.countExamRecordsOfExams(examIds));
    }

    /**
     * 查询指定考试的问答题的非空作答内容
     *
     * 网址: http://localhost:8080/api/exam/exams/{examId}/essayQuestions/{questionId}/answers
     * 参数: 无
     *
     * @param examId     考试 ID
     * @param questionId 题目 ID
     * @return payload 为作答内容的数组
     */
    @GetMapping(Urls.API_EXAM_ESSAY_QUESTION_ANSWERS)
    public Result<List<String>> findEssayQuestionAnswers(@PathVariable long examId, @PathVariable long questionId) {
        return Result.ok(examService.findEssayQuestionAnswers(examId, questionId));
    }

    /**
     * 考试计时
     *
     * 网址: http://localhost:8080/api/exam/users/{userId}/exams/{examId}/records/{recordId}/tick
     * 参数: timeInSeconds: 计时时间，单位为秒
     *
     * @param recordId      考试记录 ID
     * @param timeInSeconds 计时时间，单位为秒
     */
    @PostMapping(Urls.API_EXAM_RECORDS_TICK)
    public Result<Boolean> tickExamRecord(@PathVariable long recordId, @RequestParam int timeInSeconds) {
        examDao.increaseExamRecordElapsedTime(recordId, timeInSeconds);
        return Result.ok();
    }
}
