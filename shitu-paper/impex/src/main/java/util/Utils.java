package util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class Utils {
    /**
     * 从 CSV 的知识点文件的名字中提取知识点的科目名称
     * 文件名格式为: 高中语文-GYWT033C.csv
     *
     * @param csvKpFileName CSV 的知识点文件的名字
     * @return 返回知识点的科目名称
     */
    public static String getKpSubjectName(String csvKpFileName) {
        int hyphenIndex = csvKpFileName.indexOf("-");
        return csvKpFileName.substring(0, hyphenIndex);
    }

    /**
     * 从 CSV 的知识点文件的名字中提取知识点的科目编码
     * 文件名格式为: 高中语文-GYWT033C.csv
     *
     * @param csvKpFileName CSV 的知识点文件的名字
     * @return 返回知识点的科目编码
     */
    public static String getKpSubjectCode(String csvKpFileName) {
        int hyphenIndex = csvKpFileName.indexOf("-");
        int dotIndex = csvKpFileName.indexOf(".");
        return csvKpFileName.substring(hyphenIndex+1, dotIndex);
    }

    /**
     * 查找科目编码对应的科目名称
     *
     * @param subjectCode 科目编码
     * @return 返回科目名称
     */
    public static String findSubjectName(String subjectCode) {
        return subjects.get(subjectCode);
    }

    /**
     * 导出知识点的 SQL 语句
     */
    public static void outputDumpKnowledgePointSqls() {
        // sqlcmd -S localhost -d GYWT033C -E -o "C:/kp/高中语文-GYWT033C.csv" -Q "select * from KPVCPInfo" -W -w 999 -s ","
        subjects.forEach((key, value) -> {
            System.out.printf("sqlcmd -S localhost -d %s -E -o \"C:/kp/%s.csv\" -Q \"select * from KPVCPInfo\" -W -w 999 -s \",\"\n",
                    key, value + "-" + key);
        });
    }

    // 科目编码和名称的 map
    private static Map<String, String> subjects = new HashMap<>();

    static {
        subjects.put("GYWT033C", "高中语文");
        subjects.put("GDLT030C", "高中地理");
        subjects.put("GLST033C", "高中历史");
        subjects.put("GZSX060B", "高中数学");
        subjects.put("GZYW033C", "高中语文");
        subjects.put("GZLS033C", "高中历史");
        subjects.put("GDLZ033C", "高中地理");
        subjects.put("GZSW033C", "高中生物");
        subjects.put("GYYK034C", "高中英语");
        subjects.put("GZDL033C", "高中地理");
        subjects.put("GZHX062A", "高中化学");
        subjects.put("GZYY033C", "高中英语");
        subjects.put("GSZH030C", "高中数学");
        subjects.put("GZZZ033C", "高中政治");
        subjects.put("GZWL061A", "高中物理");
        subjects.put("GZWL061B", "高中物理");
        subjects.put("GSWT033C", "高中生物");
        subjects.put("ZSWZ033C", "高中生物");
    }
}
