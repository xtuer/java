package com.exam.controller;

import com.exam.bean.Page;
import com.exam.bean.Result;
import com.exam.bean.exam.Paper;
import com.exam.mapper.exam.PaperMapper;
import com.exam.service.exam.PaperService;
import com.exam.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class PaperController extends BaseController {
    @Autowired
    private PaperService paperService;

    @Autowired
    private PaperMapper paperMapper;

    /**
     * 查询指定 ID 的试卷
     *
     * 网址: http://localhost:8080/api/exam/papers/{paperId}
     * 参数: 无
     *
     * @param paperId 试卷 ID
     * @return payload 为试卷
     */
    @GetMapping(Urls.API_PAPERS_BY_ID)
    public Result<Paper> findPaperById(@PathVariable long paperId) {
        Paper paper = paperService.findPaper(paperId);

        if (paper != null) {
            return Result.ok(paper);
        } else {
            return Result.failMessage("查询不到 ID 为 " + paperId + " 的试卷");
        }
    }

    /**
     * 更新创建试卷
     *
     * 网址: http://localhost:8080/api/exam/papers/ofCurrentOrg
     * 参数:
     *     title     [可选]: 试卷标题，可模糊搜索
     *     pageSize  [可选]: 数量
     *     pageNumber[可选]: 页码
     *
     * @param title 试卷标题
     * @param page  分页对象
     * @return payload 为试卷的 ID
     */
    @GetMapping(Urls.API_PAPERS_OF_CURRENT_ORG)
    public Result<List<Paper>> findPapersOfCurrentOrg(@RequestParam(required = false) String title, Page page) {
        long orgId = super.getCurrentOrganizationId();
        return Result.ok(paperMapper.findPapersByOrgId(orgId, title, page));
    }

    /**
     * 更新创建试卷
     *
     * 网址: http://localhost:8080/api/exam/papers/{paperId}
     * 参数: 无
     * RequestBody 为 JSON 格式的试卷
     *
     * @param paper 试卷
     * @return payload 为试卷的 ID
     */
    @PutMapping(Urls.API_PAPERS_BY_ID)
    public Result<Long> upsertPaper(@RequestBody Paper paper) {
        // 设置试卷的机构 ID 为当前域名机构的 ID
        long orgId = super.getCurrentOrganizationId();
        paper.setOrgId(orgId);

        return Result.ok(paperService.upsertPaper(paper));
    }
}
