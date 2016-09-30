package com.ebag.bean.realexam;

import com.github.sd4324530.fastexcel.FastExcel;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class ExamScoreUtils {
    /**
     * 从 Excel 中读取成绩
     * @return
     * @throws Exception
     */
    public static List<ExamScore> readExamsScoresFromExcel(InputStream in, String examName) throws Exception {
        FastExcel fastExcel = new FastExcel(in);
        fastExcel.setSheetName("Sheet1");
        List<RawExamScores> rawScores = fastExcel.parse(RawExamScores.class); // FastExcel 使用注解得到了所有的原始成绩

        List<ExamScore> examScores = new LinkedList<ExamScore>(); // 所有学生所有科目的成绩

        // 把原始的成绩分割为每个科目的成绩
        if(null != rawScores && !rawScores.isEmpty()) {
            for(RawExamScores rawScore : rawScores) {
                rawScore.setExamName(examName);

                if (rawScore.getStudentNumber() != null) {
                    examScores.addAll(ExamScoreUtils.rawExamScoreToExamScores(rawScore));
                }
            }
        }

        return examScores;
    }

    /**
     * 把 ExamScore 的 list 转换为树结构化，有层次的成绩对象，这样在生成 JSON 时更好一些
     *
     * @param studentScores 同一个学生的成绩 ExamScore 对象的 list
     * @return 结构化的 ExamScores 对象
     */
    public static ExamScores examScoresToStructuredExamScores(List<ExamScore> studentScores, int studentCount) {
        ExamScores scores = new ExamScores();

        for (ExamScore score : studentScores) {
            scores.setStudentName(score.getStudentName());
            scores.setStudentNumber(score.getStudentNumber());
            scores.setStudentCount(studentCount);

            Subject subject = null;

            // 查找科目，如果没有则创建，判断条件：科目的名字相同
            for (Subject tempSubject : scores.getSubjects()) {
                if (score.getSubjectName().equals(tempSubject.getName())) {
                    subject = tempSubject;
                }
            }

            if (subject == null) {
                subject = new Subject();
                subject.setName(score.getSubjectName());
                scores.getSubjects().add(subject);
            }

            // 添加成绩到科目里
            subject.getScores().add(score);
        }

        return scores;
    }

    /**
     * ExamScores 中有些属性是不需要在 JSON 里显示的，把其设置为 null.
     * Jackson 在把对象转换为 JSON 时，会忽略为 null 的属性.
     *
     * @param examScores
     */
    public static void filterExamScoreFieldsForJson(ExamScores examScores) {
        for (Subject temp : examScores.getSubjects()) {
            for (ExamScore s : temp.getScores()) {
                // 学生名字，学号，科目不需要再显示
                s.setStudentName(null);
                s.setStudentNumber(null);
                s.setSubjectName(null);
            }
        }
    }

    /**
     * Excel 中每一行为一个学生一次考试所有成绩，rawExamScoreToExamScores() 把其分割成单独的学科与成绩
     *
     * @param rawScores     Excel 中每一行为一个学生一次考试所有科目的成绩
     * @return 单独学科成绩的 list
     */
    public static List<ExamScore> rawExamScoreToExamScores(RawExamScores rawScores) {
        List<ExamScore> scores = new LinkedList<ExamScore>();

        // 学号为 null 或者为空则返回
        if (rawScores.getStudentNumber() == null || "".equals(rawScores.getStudentNumber())) {
            return scores;
        }

        String examName      = rawScores.getExamName();
        String studentNumber = rawScores.getStudentNumber();
        String studentName   = rawScores.getStudentName();

        ExamScore chineseScore   = new ExamScore(examName, studentNumber, studentName, "语文", rawScores.getChineseScore(), rawScores.getChineseRank());
        ExamScore mathScore      = new ExamScore(examName, studentNumber, studentName, "数学", rawScores.getMathScore(), rawScores.getMathRank());
        ExamScore englishScore   = new ExamScore(examName, studentNumber, studentName, "英语", rawScores.getEnglishScore(), rawScores.getEnglishRank());
        ExamScore physicsScore   = new ExamScore(examName, studentNumber, studentName, "物理", rawScores.getPhysicsScore(), rawScores.getPhysicsRank());
        ExamScore chemistryScore = new ExamScore(examName, studentNumber, studentName, "化学", rawScores.getChemistryScore(), rawScores.getChemistryRank());
        ExamScore biologyScore   = new ExamScore(examName, studentNumber, studentName, "生物", rawScores.getBiologyScore(), rawScores.getBiologyRank());
        ExamScore historyScore   = new ExamScore(examName, studentNumber, studentName, "历史", rawScores.getHistoryScore(), rawScores.getHistoryRank());
        ExamScore geographyScore = new ExamScore(examName, studentNumber, studentName, "地理", rawScores.getGeographyScore(), rawScores.getGeographyRank());
        ExamScore politicsScore  = new ExamScore(examName, studentNumber, studentName, "政治", rawScores.getPoliticsScore(), rawScores.getPoliticsRank());
        ExamScore totalScore     = new ExamScore(examName, studentNumber, studentName, "总分", rawScores.getTotalScore(), rawScores.getTotalRank());
        ExamScore artsTotalScore = new ExamScore(examName, studentNumber, studentName,
                "文科总分", rawScores.getArtsTotalScore(), rawScores.getArtsTotalRank());
        ExamScore scienceTotalScore = new ExamScore(examName, studentNumber, studentName,
                "理科总分", rawScores.getScienceTotalScore(), rawScores.getScienceTotalRank());

        scores.add(chineseScore);
        scores.add(mathScore);
        scores.add(englishScore);
        scores.add(physicsScore);
        scores.add(chemistryScore);
        scores.add(biologyScore);
        scores.add(historyScore);
        scores.add(geographyScore);
        scores.add(politicsScore);
        scores.add(totalScore);
        scores.add(artsTotalScore);
        scores.add(scienceTotalScore);

        return scores;
    }
}
