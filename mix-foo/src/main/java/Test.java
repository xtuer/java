import bean.ChapterKnowledgePoint;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) throws Exception {
        File ckpsFile = new File("/Users/Biao/Desktop/chapter-kps.xlsx");
        List<ChapterKnowledgePoint> ckps = importCkps(ckpsFile);
        System.out.println(JSON.toJSONString(ckps));
    }

    /**
     * 从 Excel 中导入章节知识点关系
     *
     * @param ckpsFile Excel 文件
     * @return 返回章节知识点关系的数组
     */
    public static List<ChapterKnowledgePoint> importCkps(File ckpsFile) {
        ImportParams params = new ImportParams();
        List<ChapterKnowledgePoint> ckps = ExcelImportUtil.importExcel(ckpsFile, ChapterKnowledgePoint.class, params);

        ckps = ckps.stream().filter(ckp -> !ckp.getBookAndChapterCode().isEmpty()).peek(ckp -> {
            // [1] 分离 bookCode 和 chapterCode: gz_hx_rjb_bx01:01-01
            String[] codes = StringUtils.split(ckp.getBookAndChapterCode(), ":");
            ckp.setBookCode(codes[0].trim());
            ckp.setChapterCode(codes[1].trim());

            // [2] 分离知识点: 物质的提纯方法，10010402019941；粗盐提纯，10010402020241；
            String[] tokens = StringUtils.split(ckp.getKnowledgePointsString(), "；");

            Stream.of(tokens).filter(StringUtils::isNotBlank).forEach(token -> {
                String[] temp = StringUtils.split(token, "，");
                ckp.getKnowledgePoints().put(temp[1].trim(), temp[0].trim());
            });

            // [3] 去掉无用的数据
            ckp.setBookAndChapterCode(null);
            ckp.setKnowledgePointsString(null);
        }).collect(Collectors.toList());

        return ckps;
    }
}
