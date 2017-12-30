package com.xtuer.controller;

import com.xtuer.bean.Question;
import com.xtuer.bean.QuestionKnowledgePoint;
import com.xtuer.bean.Result;
import com.xtuer.mapper.QuestionMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Controller
public class QuestionController {
    private static Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "config")
    private Properties config;

    @Autowired
    private QuestionMapper questionMapper;

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
     * 查找知识点下的单题
     * URL: http://localhost:8080/rest/questionKnowledgePoints/128344304602054663/questions
     *
     * @param questionKnowledgePointId 知识点的 ID
     */
    @GetMapping(UriView.REST_QUESTIONS_UNDER_KNOWLEDGE_POINT)
    @ResponseBody
    public Result<List<Question>> findQuestionsByQuestionKnowledgePointId(@PathVariable Long questionKnowledgePointId) {
        return Result.ok("success", questionMapper.findQuestionsByQuestionKnowledgePointId(questionKnowledgePointId));
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
    @ResponseBody
    public Result<List<String>> findMarkedQuestionOriginalIds() {
        return Result.ok("success", questionMapper.findMarkedQuestionOriginalIds());
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

    @GetMapping("/rest/questionCountOfQuestionKnowledgePoint")
    @ResponseBody
    public Result<List<Map<Long, Integer>>> questionCountOfQuestionKnowledgePoint() {
        return Result.ok("success", questionMapper.questionCountOfQuestionKnowledgePoint());
    }
}
