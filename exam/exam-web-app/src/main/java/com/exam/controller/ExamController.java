package com.exam.controller;

import com.exam.bean.Result;
import com.exam.bean.exam.Exam;
import com.exam.bean.exam.ExamRecord;
import com.exam.mapper.exam.ExamMapper;
import com.exam.service.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考试的控制器
 */
@RestController
public class ExamController extends BaseController {
    @Autowired
    private ExamService examService;

    @Autowired
    private ExamMapper examMapper;

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
    public Result<Exam> findUserExam(@PathVariable long userId, @PathVariable long examId) {
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
        return examService.insertExamRecord(userId, examId);
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
}
