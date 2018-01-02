package com.xtuer.controller;

import com.xtuer.bean.Question;
import com.xtuer.bean.QuestionKnowledgePoint;
import com.xtuer.bean.Result;
import com.xtuer.mapper.QuestionMapper;
import com.xtuer.util.PageUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

@Controller
public class QuestionController {
    private static Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "config")
    private Properties config;

    @Autowired
    private QuestionMapper questionMapper;

    /**
     * 取得所有的知识点
     * URL: http://localhost:8080/rest/questionKnowledgePoints
     */
    @GetMapping(UriView.REST_QUESTION_KNOWLEDGE_POINTS)
    @ResponseBody
    public Result<List<QuestionKnowledgePoint>> findAllQuestionKnowledgePoints() {
        List<QuestionKnowledgePoint> kps = questionMapper.findAllQuestionKnowledgePoints();
        return Result.ok("success", kps);
    }

    /**
     * 查找知识点
     * URL: http://localhost:8080/rest/questionKnowledgePoints/128344304878878720/children
     *
     * @param parentId 父知识点的 ID
     */
    @GetMapping(UriView.REST_QUESTION_KNOWLEDGE_POINTS_BY_PARENT_ID)
    @ResponseBody
    public Result<List<QuestionKnowledgePoint>> findQuestionKnowledgePointsByParentId(@PathVariable Long parentId) {
        List<QuestionKnowledgePoint> kps = questionMapper.findQuestionKnowledgePointsByParentId(parentId);
        return Result.ok("success", kps);
    }

    /**
     * 查找科目下的知识点
     * URL: http://localhost:8080/rest/subjectCodes/GYYK034C/questionKnowledgePoints
     *
     * @param subjectCode 知识点的科目编码
     */
    @GetMapping(UriView.REST_QUESTION_KNOWLEDGE_POINTS_BY_SUBJECT_CODE)
    @ResponseBody
    public Result<List<QuestionKnowledgePoint>> findQuestionKnowledgePointsBySubjectCode(@PathVariable String subjectCode) {
        List<QuestionKnowledgePoint> kps = questionMapper.findQuestionKnowledgePointsBySubjectCode(subjectCode);
        return Result.ok("success", kps);
    }

    /**
     * 查找知识点下的单题
     * URL: http://localhost:8080/rest/questionKnowledgePoints/128344304602054663/questions?pageNumber=2&pageSize=30
     * 参数: 页码
     *
     * @param questionKnowledgePointId 知识点的 ID
     */
    @GetMapping(UriView.REST_QUESTIONS_UNDER_KNOWLEDGE_POINT)
    @ResponseBody
    public Result<List<Question>> findQuestionsByQuestionKnowledgePointId(@PathVariable Long questionKnowledgePointId,
                                                                          @RequestParam(defaultValue="1") int pageNumber,
                                                                          @RequestParam(defaultValue="30") int pageSize) {
        int offset = PageUtils.offset(pageNumber, pageSize);
        return Result.ok("success", questionMapper.findQuestionsByQuestionKnowledgePointId(questionKnowledgePointId, offset, pageSize));
    }

    /**
     * 标记和取消标记题目
     * URL: http://localhost:8080/rest/questions/129751762465718272/toggleMark
     *
     * @param questionId 题目的 ID
     */
    @PutMapping(UriView.REST_TOGGLE_QUESTION_MARK)
    @ResponseBody
    public Result toggleQuestionMark(@PathVariable Long questionId) {
        questionMapper.toggleQuestionMark(questionId);
        return Result.ok();
    }

    /**
     * 查找所有被标记过的题目的原始 ID
     * URL: http://localhost:8080/rest/questionIds/marked
     */
    @GetMapping(UriView.REST_MARKED_QUESTION_IDS)
    public void exportMarkedQuestionOriginalIds(HttpServletResponse response) {
        OutputStream out = null;
        try {
            // You must tell the browser the file type you are going to send
            // for example application/pdf, text/plain, text/html, image/jpg
            response.setContentType("application/octet-stream"); // 以流的形式下载文件
            response.setHeader("Content-Disposition", "attachment;filename=question-ids.txt"); // 下载保存时的文件名
            // response.setContentLength(length); // [可选] 设置文件的大小，浏览器就能够知道下载进度条了
            out = response.getOutputStream();
            IOUtils.writeLines(questionMapper.findMarkedQuestionOriginalIds(), "\n", out);
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
        } finally {
            IOUtils.closeQuietly(out);
        }

    }

    /**
     * 读取题目的图片返回给浏览器
     *
     * @param subjectCode 科目编码
     * @param imageName   图片名字
     * @param response    HttpServletResponse
     */
    @GetMapping("/question-img/{subjectCode}/{imageName:.+}")
    public void readQuestionImage(@PathVariable String subjectCode, @PathVariable String imageName, HttpServletResponse response) {
        InputStream in = null;
        OutputStream out = null;

        try {
            String ext = FilenameUtils.getExtension(imageName);
            response.setContentType("image/" + ext); // 如果是 jpg 则为 image/jpeg，svg 为 image/svg+xml 等

            File subjectImageDirectory = new File(config.getProperty("question.imageDirectory"), subjectCode);
            File imageFile = new File(subjectImageDirectory, imageName);
            in = new FileInputStream(imageFile);
            out = response.getOutputStream();
            IOUtils.copy(in, out);
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 获取某个知识点下题目的页数
     * URL: http://localhost:8080/rest/questionKnowledgePoints/{questionKnowledgePointId}/questions/count?pageSize=30
     * 参数: pageSize，如果没有，默认为 30
     *
     * @param questionKnowledgePointId
     */
    @GetMapping(UriView.REST_QUESTIONS_PAGE_COUNT_UNDER_KNOWLEDGE_POINT)
    @ResponseBody
    public Result<Integer> questionPageCountOfQuestionKnowledgePoint(@PathVariable Long questionKnowledgePointId,
                                                                     @RequestParam(defaultValue = "30") int pageSize) {
        int recordCount = questionMapper.questionsCountByQuestionKnowledgePointId(questionKnowledgePointId);
        int pageCount = PageUtils.pageCount(recordCount, pageSize);
        return Result.ok("success",pageCount );
    }

    /**
     * 更新知识点下的题目数量
     * URL: http://localhost:8080/rest/questionKnowledgePoints/count
     */
    @PutMapping(UriView.REST_QUESTION_KNOWLEDGE_POINTS_COUNT)
    @ResponseBody
    public Result updateQuestionCount() {
        // 1. 先设置 count 为 0
        // 2. 题目表使用知识点 ID 分组，统计每个分组下有多少个题目，然后更新到知识点表
        questionMapper.cleanQuestionCount();
        questionMapper.updateQuestionCount();

        return Result.ok();
    }

    /**
     * 查找科目下没有知识点的题目数量
     *
     * @param subjectCode 科目的编码
     * @param pageSize    每页的数量
     */
    @GetMapping(UriView.REST_NO_KNOWLEDGE_POINT_QUESTIONS_PAGE_COUNT_UNDER_SUBJECT)
    @ResponseBody
    public Result<Integer> noKnowledgePointQuestionsPageCountBySubjectCode(@PathVariable String subjectCode,
                                                                       @RequestParam(defaultValue = "30") int pageSize) {
        int recordCount = questionMapper.noKnowledgePointQuestionsCountBySubjectCode(subjectCode);
        int pageCount = PageUtils.pageCount(recordCount, pageSize);
        return Result.ok("success",pageCount );
    }

    /**
     * 查找科目下没有知识点的题目
     *
     * @param subjectCode 科目的编码
     * @param pageNumber  第几页
     * @param pageSize    每页的数量
     */
    @GetMapping(UriView.REST_NO_KNOWLEDGE_POINT_QUESTIONS_UNDER_SUBJECT)
    @ResponseBody
    public Result<List<Question>> findNoKnowledgePointQuestionsBySubjectCode(@PathVariable String subjectCode,
                                                                             @RequestParam(defaultValue="1") int pageNumber,
                                                                             @RequestParam(defaultValue="30") int pageSize) {
        int offset = PageUtils.offset(pageNumber, pageSize);
        List<Question> questions = questionMapper.findNoKnowledgePointQuestionsBySubjectCode(subjectCode, offset, pageSize);
        return Result.ok("success", questions);
    }
}
