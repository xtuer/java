import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileHandler {
    public static void main(String[] args) {
        outputDumpKnowledgePointSqls();
    }

    public static void listKPFiles() {
        File holder = new File("/Users/Biao/Documents/套卷/知识点");
        File[] files = holder.listFiles((d, name) -> {
            return name.endsWith(".csv");
        });

        for (File file : files) {
            String path = file.getAbsolutePath();
            String fileName = file.getName();
            int hyphenIndex = fileName.indexOf("-");
            int dotIndex = fileName.indexOf(".");

            String subject = fileName.substring(0, hyphenIndex);
            String subjectCode = fileName.substring(hyphenIndex + 1, dotIndex);

            System.out.printf("{\"%s\", \"%s\", \"%s\"},\n", path, subject, subjectCode);
        }
    }

    public static void listIdFiles() {
        File holder = new File("/Users/Biao/Documents/套卷/题目ID");
        File[] subjects = holder.listFiles((d, name) -> {
            return !name.equals("readme.txt");
        });

        for (File subject : subjects) {
            File[] idFiles = subject.listFiles();
            for (File idFile : idFiles) {
                String subjectLabel = subject.getName();
                String subjectCode = FilenameUtils.getBaseName(idFile.getName());
                System.out.printf("{\"%s\", \"F:/题目ID/%s/%s.csv\", \"F:/题目/%s/%s\"},\n",
                        subjectCode, subjectLabel, subjectCode, subjectLabel, subjectCode);
            }
        }
    }

    /**
     * 导出知识点的 SQL 语句
     */
    public static void outputDumpKnowledgePointSqls() {
        // sqlcmd -S localhost -d GZHX062A -E -o "C:/kp/GZHX062A.csv" -Q "select * from KPVCPInfo" -W -w 999 -s ","
        subjects.forEach((key, value) -> {
            System.out.printf("sqlcmd -S localhost -d %s -E -o \"C:/kp/%s.csv\" -Q \"select * from KPVCPInfo\" -W -w 999 -s \",\"\n",
                    key, value + "-" + key);
        });
    }

    /**
     * 查找科目编码对应的科目名称
     * @param subjectCode 科目编码
     * @return 科目
     */
    public static String findSubject(String subjectCode) {
        return subjects.get(subjectCode);
    }

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
