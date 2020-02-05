package bean;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * 章节知识点关系: 一个章节可能有多个相关的知识点
 */
@Getter
@Setter
@Accessors(chain = true)
public final class ChapterKnowledgePoint {
    @Excel(name = "章节编码")
    private String bookAndChapterCode; // 提示: 导入时使用

    private String bookCode;
    private String chapterCode;

    @Excel(name = "章节注释")
    private String chapterComment;

    @Excel(name = "知识点编码")
    private String knowledgePointsString; // 提示: 导入时使用

    // 知识点: key(编码), value(名字)
    private Map<String, String> knowledgePoints = new HashMap<>();
}
