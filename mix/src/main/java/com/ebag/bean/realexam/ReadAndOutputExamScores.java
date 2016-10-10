package com.ebag.bean.realexam;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

// FastExcel 主页: http://git.oschina.net/pyinjava/fastexcel
// 需要的依赖 Gradle Dependency: compile 'com.github.sd4324530:fastexcel:0.0.4'

public class ReadAndOutputExamScores {
    public static void main(String[] args) throws Exception {
        // [[1]] 从 Excel 中导入成绩
        InputStream in = ReadAndOutputExamScores.class.getClassLoader().getResourceAsStream("期中考试.xlsx"); // InputStream 可以从上传文件流中得到
        String examName = "期中考试"; // 上传的文件名去掉后缀，规定文件名代表考试的名字，例如期中考试，期末考试
        List<ExamScore> importedExamScores = ExamScoreUtils.readExamsScoresFromExcel(in, examName);
        in.close();

        // [[2]] TODO: 保存 examScores 到数据库

        // [[3]] 查找学号为 182188 的学生的成绩，然后输出为 JSON
        List<ExamScore> foundExamScores = findExamScoresByStudentNumber(importedExamScores, "182188"); // TODO: 模拟从数据库中使用学号查找成绩
        int studentCount = 700; // TODO: 学生数量为最大排名数，辅助显示曲线，不需要非常准确，这个数字取数据库里最大的排名值(每个年级的学生数量都差不多，所以可以简单的使用，这个数也只是参考值)
        ExamScores studentExamScores = ExamScoreUtils.examScoresToStructuredExamScores(foundExamScores, studentCount); // 用于生成成绩的 JSON 数据的对象
        ExamScoreUtils.filterExamScoreFieldsForJson(studentExamScores); // 过滤不需要显示在 JSON 中的属性

        // TODO: Spring 把 studentExamScores 返回给移动端，注册的 JSON 工具会自动转换为 JSON 格式
        System.out.println(JsonUtil.toJson(studentExamScores)); // 输出为 json 格式
    }

    /**
     * 模拟从数据库中使用学号查找成绩
     *
     * @param examScores 所有成绩的 list，模拟数据库里的所有成绩记录
     * @param studentNumber 学号
     * @return 学生的所有成绩
     */
    public static List<ExamScore> findExamScoresByStudentNumber(List<ExamScore> examScores, String studentNumber) {
        List<ExamScore> foundScores = new LinkedList<ExamScore>();
        for (ExamScore score : examScores) {
            if (studentNumber.equals(score.getStudentNumber())) {
                foundScores.add(score);
            }
        }

        return foundScores;
    }
}
