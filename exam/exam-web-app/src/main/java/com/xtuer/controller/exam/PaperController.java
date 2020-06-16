package com.xtuer.controller.exam;

import com.xtuer.bean.Page;
import com.xtuer.bean.Result;
import com.xtuer.bean.Urls;
import com.xtuer.bean.exam.Paper;
import com.xtuer.controller.BaseController;
import com.xtuer.mapper.exam.PaperMapper;
import com.xtuer.service.exam.PaperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 试卷的控制器
 */
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
        return Result.single(paper, "试卷不存在: " + paperId);
    }

    /**
     * 查询当前机构的试卷
     *
     * 网址: http://localhost:8080/api/exam/papers/ofCurrentOrg
     * 参数:
     *     type      [可选]: 试卷类型: 0 (普通试卷)、1 (调查问卷)
     *     title     [可选]: 试卷标题，可模糊搜索
     *     pageSize  [可选]: 数量
     *     pageNumber[可选]: 页码
     *
     * @param title 试卷标题
     * @param page  分页对象
     * @return payload 为试卷的 ID
     */
    @GetMapping(Urls.API_PAPERS_OF_CURRENT_ORG)
    public Result<List<Paper>> findPapersOfCurrentOrg(@RequestParam int type, @RequestParam(required = false) String title, Page page) {
        long orgId = super.getCurrentOrganizationId();
        return Result.ok(paperMapper.findPapersByHolderId(orgId, type, title, page));
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
        // 设置试卷的拥有者 ID 为当前域名机构的 ID，实际项目中可根据业务需求进行调整
        long holderId = super.getCurrentOrganizationId();
        paper.setHolderId(holderId);

        return Result.ok(paperService.upsertPaper(paper));
    }

    /**
     * 删除试卷
     *
     * 网址: http://localhost:8080/api/exam/papers/{paperId}
     * 参数: 无
     *
     * @param paperId 试卷 ID
     */
    @DeleteMapping(Urls.API_PAPERS_BY_ID)
    public Result<Boolean> deletePaper(@PathVariable long paperId) {
        paperService.deletePaper(paperId);
        return Result.ok();
    }
}
