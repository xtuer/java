package com.xtuer.controller;

import com.xtuer.bean.KnowledgePoint;
import com.xtuer.bean.Paper;
import com.xtuer.bean.Result;
import com.xtuer.mapper.PaperDirectoryMapper;
import com.xtuer.mapper.PaperMapper;
import com.xtuer.service.PaperService;
import com.xtuer.util.ContentType;
import com.xtuer.util.PageUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

@Controller
public class PaperController {
    private static Logger logger = LoggerFactory.getLogger(PaperController.class);

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private PaperDirectoryMapper directoryMapper;

    @Autowired
    private PaperService paperService;

    /**
     * URL: http://localhost:8080/rest/paperDirectories/papers/1
     *
     * @param paperId 试卷 id
     * @return
     */
    @GetMapping(UriView.REST_PAPERS_BY_ID)
    @ResponseBody
    public Result<Paper> papersById(@PathVariable String paperId) {
        return Result.ok("", paperMapper.findPaperByPaperId(paperId));
    }

    @PutMapping(UriView.REST_PAPERS_BY_ID)
    @ResponseBody
    public Result updatePaper(Paper paper) {
        paperMapper.updatePaper(paper);

        return Result.ok();
    }

    /**
     * 查找目录中第 page 页的试卷，page 默认为 1
     * URL: http://localhost:8080/rest/paperDirectories/0/papers?page=2
     * 参数: page(可选), size(可选)
     *
     * @param paperDirectoryId 目录 id
     * @param pageNumber 页数
     * @return
     */
    @GetMapping(UriView.REST_PAPERS_OF_DIRECTORY)
    @ResponseBody
    public Result<List<Paper>> papersOfDirectory(@PathVariable String paperDirectoryId,
                                                 @RequestParam(required=false, defaultValue="1") int pageNumber,
                                                 @RequestParam(required=false, defaultValue="50") int pageSize) {
        int offset = PageUtils.offset(pageNumber, pageSize);
        List<Paper> papers = paperMapper.findPapersByPaperDirectoryId(paperDirectoryId, offset, pageSize);

        return Result.ok("", papers);
    }

    /**
     * 设置试卷的目录
     * URL: http://localhost:8080/rest/paperDirectories/papers/re-directory
     * 参数: paperDirectoryId, paperIds: paperDirectoryId=122&paperIds[]=1&paperIds[]=2&paperIds[]=3
     *
     * @param paperDirectoryId 试卷所在目录 id
     * @param paperIds 书卷 id
     * @return
     */
    @PutMapping(UriView.REST_PAPERS_REDIRECTORY)
    @ResponseBody
    public Result movePapersInPaperDirectory(@RequestParam String paperDirectoryId, @RequestParam("paperIds[]") List<String> paperIds) {
        if (directoryMapper.isPaperDirectoryExisting(paperDirectoryId)) {
            paperMapper.setPapersPaperDirectory(paperDirectoryId, paperIds);
            return Result.ok();
        } else {
            return Result.fail("试卷目录不存在");
        }
    }

    /**
     * 计算目录中试卷的个数
     * URL: http://localhost:8080/rest/paperDirectories/0/papers/count
     *
     * @param paperDirectoryId 目录 id
     * @return
     */
    @GetMapping(UriView.REST_PAPERS_COUNT_OF_DIRECTORY)
    @ResponseBody
    public Result papersCountOfDirectory(@PathVariable String paperDirectoryId) {
        return Result.ok("", paperMapper.papersCountByPaperDirectoryId(paperDirectoryId));
    }

    /**
     * 查找传入的学科 subject 中，原文件名包含 nameFilter，并且没有分配目录的试卷
     * URL: http://localhost:8080/rest/papers/search?subject=高中历史&nameFilter=2012
     *
     * @param subject    学科，例如高中物理
     * @param nameFilter 名字中包含的子串
     * @return
     */
    @GetMapping(UriView.REST_PAPERS_SEARCH)
    @ResponseBody
    public Result<List<Paper>> searchPapersNotInPaperDirectory(@RequestParam String subject, @RequestParam String nameFilter) {
        return Result.ok("", paperMapper.findPapersBySubjectAndNameFilterNotInPaperDirectory(subject, nameFilter));
    }

    /**
     * 给试卷添加知识点
     * URL: http://localhost:8080/rest/papers/23232/knowledgePoints
     * 参数: knowledgePointIds[]
     *
     * @param paperId
     * @param knowledgePointIds
     * @return
     */
    @PostMapping(UriView.REST_PAPERS_KNOWLEDGE_POINTS)
    @ResponseBody
    public Result addKnowledgePoints(@PathVariable String paperId, @RequestParam("knowledgePointIds[]") List<String> knowledgePointIds) {
        List<String> addedKnowledgePointIds = new LinkedList<>(); // 新增加的知识点 id

        for (String knowledgePointId : knowledgePointIds) {
            // 如果知识点存在则不添加
            // 如果知识点不存在则添加
            if (!paperMapper.hasKnowledgePoint(paperId, knowledgePointId)) {
                paperMapper.addKnowledgePoint(paperId, knowledgePointId);
                addedKnowledgePointIds.add(knowledgePointId);
            }
        }

        return Result.ok("知识点添加成功", addedKnowledgePointIds);
    }

    @GetMapping(UriView.REST_PAPERS_KNOWLEDGE_POINTS)
    @ResponseBody
    public Result findKnowledgePointsByPaperId(@PathVariable String paperId) {
        List<KnowledgePoint> points = paperMapper.findKnowledgePointsByPaperId(paperId);
        return Result.ok("", points);
    }

    @DeleteMapping(UriView.REST_PAPERS_KNOWLEDGE_POINTS_BY_ID)
    @ResponseBody
    public Result deleteKnowledgePoint(@PathVariable String paperId, @PathVariable String knowledgePointId) {
        paperMapper.deleteKnowledgePoint(paperId, knowledgePointId);
        return Result.ok();
    }

    /**
     * 查找目录下带知识点的试卷
     * URL: http://localhost:8080/rest/paperDirectories/{paperDirectoryId}/papers/search
     * 参数: knowledgePointIds
     *
     * @param paperDirectoryId
     * @param knowledgePointIds
     * @return
     */
    @GetMapping(UriView.REST_PAPERS_SEARCH_IN_DIRECTORY)
    @ResponseBody
    public Result findPapersByKnowledgePointIdInPaperDirectory(@PathVariable String paperDirectoryId,
                                                               @RequestParam(value="knowledgePointIds[]", required=false) List<String> knowledgePointIds,
                                                               @RequestParam(required=false, defaultValue="1") int pageNumber,
                                                               @RequestParam(required=false, defaultValue="50") int pageSize) {
        List<Paper> papers;
        int offset = PageUtils.offset(pageNumber, pageSize);

        // 如果没有知识点，则只从目录中查找
        if (knowledgePointIds == null || knowledgePointIds.isEmpty()) {
            papers = paperMapper.findPapersByPaperDirectoryId(paperDirectoryId, offset, pageSize);
        } else {
            papers = paperMapper.findPapersByPaperDirectoryIdWithKnowledgePointIds(paperDirectoryId, knowledgePointIds, offset, pageSize);
        }

        return Result.ok("", papers);
    }

    /**
     * 目录下带知识点的试卷的页数
     * URL: http://localhost:8080/rest/paperDirectories/{paperDirectoryId}/papers/countAsSearch
     *
     * @param paperDirectoryId
     * @param knowledgePointIds
     * @param pageSize
     * @return
     */
    @GetMapping(UriView.REST_PAPERS_COUNT_SEARCH_IN_DIRECTORY)
    @ResponseBody
    public Result pageCountPapersByKnowledgePointIdInPaperDirectory(@PathVariable String paperDirectoryId,
                                                                   @RequestParam(value="knowledgePointIds[]", required=false) List<String> knowledgePointIds,
                                                                   @RequestParam int pageSize) {
        int paperCount = 0;

        // 如果没有知识点，则只从目录中查找
        if (knowledgePointIds == null || knowledgePointIds.isEmpty()) {
            paperCount = paperMapper.countPapersByPaperDirectoryId(paperDirectoryId);
        } else {
            paperCount = paperMapper.countPapersByPaperDirectoryIdWithKnowledgePointIds(paperDirectoryId, knowledgePointIds);
        }

        int pageCount = PageUtils.pageCount(paperCount, pageSize);

        return Result.ok("", pageCount);
    }

    /**
     * 预览试卷
     *
     * @param paperId 试卷 id
     * @return 预览的 URL
     */
    @GetMapping(UriView.REST_PAPERS_PREVIEW)
    @ResponseBody
    public Result paperPreviewUrl(@PathVariable String paperId) throws Exception {
        String url = paperService.getPaperPreviewUrl(paperId);

        if (url != null) {
            return Result.ok("", url);
        } else {
            return Result.fail("找不到试卷预览文件");
        }
    }

    @GetMapping(UriView.REST_PAPERS_DOWNLOAD)
    public void downloadPaper(@PathVariable String paperId, HttpServletResponse response) {
        InputStream in = null;
        OutputStream out = null;

        try {
            Paper paper = paperMapper.findPaperByPaperId(paperId);
            String extension = FilenameUtils.getExtension(paper.getUuidName());

            String filename = new String(paper.getOriginalName().getBytes("UTF-8"), "ISO8859_1") + "." + extension; // 解决乱码问题
            response.setContentType("application/octet-stream"); // 以流的形式下载文件
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);

            in = new FileInputStream(paperService.getPaperFile(paper));
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
     * 试卷预览 html 页面
     *
     * @param paperBaseName
     * @param response
     */
    @GetMapping(UriView.PAPER_PREVIEW_INDEX)
    public void paperPreviewIndex(@PathVariable String paperBaseName, HttpServletResponse response) {
        InputStream in = null;
        OutputStream out = null;

        try {
            response.setContentType("text/html;charset=UTF-8"); // 返回 html
            in = new FileInputStream(paperService.getPaperPreviewIndex(paperBaseName));
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
     * 试卷预览页的图片
     *
     * @param paperBaseName
     * @param imageName
     * @param response
     */
    @GetMapping(UriView.PAPER_PREVIEW_IMAGE)
    public void paperPreviewImage(@PathVariable String paperBaseName,
                                  @PathVariable String imageName, HttpServletResponse response) {
        InputStream in = null;
        OutputStream out = null;

        try {
            String imageExt = FilenameUtils.getExtension(imageName); // 图片文件名的后缀
            response.setContentType(ContentType.getContentType(imageExt)); // 返回图片
            in = new FileInputStream(paperService.getPaperPreviewImage(paperBaseName, imageName));
            out = response.getOutputStream();
            IOUtils.copy(in, out);
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
}
