package com.xtuer.controller;

import com.xtuer.bean.Paper;
import com.xtuer.bean.Result;
import com.xtuer.mapper.PaperMapper;
import com.xtuer.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PaperController {
    @Autowired
    private PaperMapper paperMapper;

    /**
     * URL: http://localhost:8080/rest/paperDirectories/papers/1
     *
     * @param paperId 试卷 id
     * @return
     */
    @GetMapping(UriView.REST_PAPERS)
    @ResponseBody
    public Result<List<Paper>> papersById(@PathVariable String paperId) {
        return Result.ok("", paperMapper.findPaperByPaperId(paperId));
    }

    /**
     * 查找目录中第 page 页的试卷，page 默认为 1
     * URL: http://localhost:8080/rest/paperDirectories/0/papers?page=2
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

        return Result.ok("", paperMapper.findPapersByPaperDirectoryId(paperDirectoryId, offset, size));
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
}
