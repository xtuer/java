package com.xtuer.controller;

import com.xtuer.bean.KnowledgePoint;
import com.xtuer.bean.Paper;
import com.xtuer.bean.Result;
import com.xtuer.mapper.PaperDirectoryMapper;
import com.xtuer.mapper.PaperMapper;
import com.xtuer.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@Controller
public class PaperController {
    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private PaperDirectoryMapper directoryMapper;

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
    public Result updatePaper(@PathVariable String paperId, Paper paper) {
        if (paperId != paper.getPaperId()) {
            return Result.fail("试卷 ID 不对");
        }
        paperMapper.updatePaper(paper);

        return Result.ok();
    }

    /**
     * 查找目录中第 page 页的试卷，page 默认为 1
     * URL: http://localhost:8080/rest/paperDirectories/0/papers?page=2
     * 参数: page(可选), size(可选)
     *
     * @param paperDirectoryId 目录 id
     * @param page 页数
     * @return
     */
    @GetMapping(UriView.REST_PAPERS_OF_DIRECTORY)
    @ResponseBody
    public Result<List<Paper>> papersOfDirectory(@PathVariable String paperDirectoryId,
                                                 @RequestParam(required=false, defaultValue="1") int page,
                                                 @RequestParam(required=false, defaultValue="50") int size) {
        int offset = PageUtils.offset(page, size);
        List<Paper> papers = paperMapper.findPapersByPaperDirectoryId(paperDirectoryId, offset, size);
        loadKnowledgePoints(papers);

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
     * 参数: knowledgePointId
     *
     * @param paperId
     * @param knowledgePointId
     * @return
     */
    @PostMapping(UriView.REST_PAPERS_KNOWLEDGE_POINTS)
    @ResponseBody
    public Result addKnowledgePoint(@PathVariable String paperId, @RequestParam String knowledgePointId) {
        // 如果知识点存在则不添加
        // 如果知识点不存在则添加
        if (paperMapper.hasKnowledgePoint(paperId, knowledgePointId)) {
            return Result.fail("知识点已经存在，不需要重复添加");
        } else {
            paperMapper.addKnowledgePoint(paperId, knowledgePointId);
            return Result.ok("知识点添加成功");
        }
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
     * 查找目录下某个知识点的试卷
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
                                                               @RequestParam(value="knowledgePointIds[]", required=false) List<String> knowledgePointIds) {
        List<Paper> papers;

        // 如果没有知识点，则只从目录中查找
        if (knowledgePointIds == null || knowledgePointIds.isEmpty()) {
            papers = paperMapper.findPapersByPaperDirectoryId(paperDirectoryId, 0, 500);
        } else {
             papers = paperMapper.findPapersByKnowledgePointIdsInPaperDirectory(paperDirectoryId, knowledgePointIds);
        }

        loadKnowledgePoints(papers);

        return Result.ok("", papers);
    }

    /**
     * 加载试卷的知识点
     *
     * @param papers 试卷的 list
     */
    public void loadKnowledgePoints(List<Paper> papers) {
        if (papers.isEmpty()) {
            return;
        }

        List<String> paperIds = new LinkedList<>();

        // 把所有试卷的 id 放入 paperIds
        for (Paper paper : papers) {
            paperIds.add(paper.getPaperId());
        }

        // 根据 paperIds 查找这些试卷的知识点
        List<KnowledgePoint> points = paperMapper.findKnowledgePointsByPaperIds(paperIds);

        // 知识点放入对应的试卷
        for (KnowledgePoint point : points) {
            for (Paper paper : papers) {
                if (point.getPaperId().equals(paper.getPaperId())) {
                    paper.getKnowledgePoints().add(point);
                }
            }
        }
    }
}
