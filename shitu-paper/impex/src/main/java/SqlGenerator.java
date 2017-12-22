import util.Utils;

public class SqlGenerator {
    public static void main(String[] args) {
        generateAnswerSqls();
    }

    /**
     * 生成导出题目信息的 SQL
     */
    public static void generateAnswerSqls() {
        Utils.subjects.forEach((key, value) -> {
            System.out.printf("bcp \"select i.I_ProblemID, i.I_Answer, i.I_ProbType, i.I_VCP, i.I_Score, i.I_DifCoef, i.I_Origin, i.I_TeachDemand from [%s].[dbo].[Indicate] i, [%s].[dbo].[ProbContent] p where i.I_ProblemID=p.PC_ProblemID FOR XML AUTO, ROOT('Root')\" queryout C:/question-info/%s.xml -S(local) -T -r -c\n", key, key, key);
        });
    }

    /**
     * 导出知识点的 SQL 语句
     */
    public static void generateKnowledgePointSqls() {
        // sqlcmd -S localhost -d GYWT033C -E -o "C:/kp/高中语文-GYWT033C.csv" -Q "select * from KPVCPInfo" -W -w 999 -s ","
        Utils.subjects.forEach((key, value) -> {
            System.out.printf("sqlcmd -S localhost -d %s -E -o \"C:/kp/%s.csv\" -Q \"select * from KPVCPInfo\" -W -w 999 -s \",\"\n",
                    key, value + "-" + key);
        });
    }
}
