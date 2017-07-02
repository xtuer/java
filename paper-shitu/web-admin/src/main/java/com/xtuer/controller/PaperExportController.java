package com.xtuer.controller;

import com.xtuer.bean.Result;
import com.xtuer.service.PaperExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PaperExportController {
    @Autowired
    private PaperExportService exportService;

    /**
     * 导出试卷
     * URL: http://localhost:8080/rest/exportPapers
     * 参数: paperDirectoryIds[] 目录 id 数组
     *
     * @param paperDirectoryIds
     * @return
     */
    @PostMapping(UriView.REST_EXPORT_PAPERS)
    @ResponseBody
    public Result exportPapers(@RequestParam("paperDirectoryIds[]") List<String> paperDirectoryIds) {
        if (exportService.isExporting()) {
            return Result.fail("正在执行导出，请稍后再操作");
        } else {
            exportService.exportPapers(paperDirectoryIds);
            return Result.ok("", PaperExportService.EXPORT_RUNNING);
        }
    }

    @GetMapping(UriView.REST_EXPORT_PAPERS_STATUS)
    @ResponseBody
    public Result exportStatus() {
        return Result.ok("", exportService.exportStatus()); // data 中返回到处状态
    }
}
