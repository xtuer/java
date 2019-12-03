package com.exam.controller;

import com.exam.bean.Result;
import com.exam.bean.User;
import com.exam.bean.exam.Exam;
import com.exam.bean.exam.ExamRecord;
import com.exam.bean.exam.ExamRecordAnswer;
import com.exam.bean.exam.QuestionForAnswer;
import com.exam.mapper.exam.ExamMapper;
import com.exam.mq.MessageProducer;
import com.exam.service.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    private MessageProducer messageProducer;

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
            return Result.failMessage("找不到考试 " + examId);
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
        User user = super.getLoginUser();
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
            return Result.failMessage("找不到考试记录 " + recordId);
        }
    }

    /**
     * 对考试记录的题目进行作答
     *
     * 网址: http://localhost:8080/api/exam/users/{userId}/exams/{examId}/records/{recordId}/answer
     * 参数: 无
     * Request Body 为:
     * {
     *     "submitted": false,
     *     "questions": [
     *          { "questionId": 0, answers: [{ "questionOptionId": 0, "content": "xxx" }, { "questionOptionId": 0, "content": "xxx" }] },
     *          { "questionId": 0, answers: [{ "questionOptionId": 0, "content": "xxx" }, { "questionOptionId": 0, "content": "xxx" }] },
     *          { "questionId": 0, answers: [{ "questionOptionId": 0, "content": "xxx" }, { "questionOptionId": 0, "content": "xxx" }] },
     *     ]
     * }
     *
     * @param examRecordAnswer 考试记录的作答
     * @return 1. 如果是交卷，则 code 为 1，否则 code 为 0
     *         2. 如果只是题目作答，则 payload 为题目 ID 的数组，方便前端从作答队列中删除此题目 (如果实现了的话)
     */
    @PostMapping(Urls.API_USER_EXAM_ANSWER_QUESTIONS)
    public Result<List<Long>> answerExamRecord(@PathVariable long recordId, @RequestBody ExamRecordAnswer examRecordAnswer) {
        // userId 和 examRecordId 在登录信息和 URL 中可以获得
        examRecordAnswer.setUserId(super.getLoginUserId());
        examRecordAnswer.setExamRecordId(recordId);
        messageProducer.sendAnswerExamRecordMessage(examRecordAnswer);

        if (examRecordAnswer.isSubmitted()) {
            return Result.ok("试卷提交成功", null, 1);
        }

        List<Long> ids = examRecordAnswer.getQuestions().stream().map(QuestionForAnswer::getQuestionId).collect(Collectors.toList());
        return Result.ok(ids);
    }
}
