package com.ebag.bean.realexam;

import com.github.sd4324530.fastexcel.annotation.MapperCell;

/**
 * Excel 中每一行为一个学生一次考试的所有成绩，类 RawExamScores 为其对应的结构
 */
public class RawExamScores {
    private String examName; // 考试名字，例如期中考试

    @MapperCell(cellName = "学号")
    private String studentNumber;
    @MapperCell(cellName = "姓名")
    private String studentName;

    @MapperCell(cellName = "语文")
    private int chineseScore;
    @MapperCell(cellName = "语文排名")
    private int chineseRank;

    @MapperCell(cellName = "数学")
    private int mathScore;
    @MapperCell(cellName = "数学排名")
    private int mathRank;

    @MapperCell(cellName = "英语")
    private int englishScore;
    @MapperCell(cellName = "英语排名")
    private int englishRank;

    @MapperCell(cellName = "物理")
    private int physicsScore;
    @MapperCell(cellName = "物理排名")
    private int physicsRank;

    @MapperCell(cellName = "化学")
    private int chemistryScore;
    @MapperCell(cellName = "化学排名")
    private int chemistryRank;

    @MapperCell(cellName = "生物")
    private int biologyScore;
    @MapperCell(cellName = "生物排名")
    private int biologyRank;

    @MapperCell(cellName = "历史")
    private int historyScore;
    @MapperCell(cellName = "历史排名")
    private int historyRank;

    @MapperCell(cellName = "地理")
    private int geographyScore;
    @MapperCell(cellName = "地理排名")
    private int geographyRank;

    @MapperCell(cellName = "政治")
    private int politicsScore;
    @MapperCell(cellName = "政治排名")
    private int politicsRank;

    @MapperCell(cellName = "文科总分")
    private int artsTotalScore;
    @MapperCell(cellName = "文科排名")
    private int artsTotalRank;

    @MapperCell(cellName = "理科总分")
    private int scienceTotalScore;
    @MapperCell(cellName = "理科排名")
    private int scienceTotalRank;

    @MapperCell(cellName = "总分")
    private int totalScore;
    @MapperCell(cellName = "总排名")
    private int totalRank;

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getChineseScore() {
        return chineseScore;
    }

    public void setChineseScore(int chineseScore) {
        this.chineseScore = chineseScore;
    }

    public int getChineseRank() {
        return chineseRank;
    }

    public void setChineseRank(int chineseRank) {
        this.chineseRank = chineseRank;
    }

    public int getMathScore() {
        return mathScore;
    }

    public void setMathScore(int mathScore) {
        this.mathScore = mathScore;
    }

    public int getMathRank() {
        return mathRank;
    }

    public void setMathRank(int mathRank) {
        this.mathRank = mathRank;
    }

    public int getEnglishScore() {
        return englishScore;
    }

    public void setEnglishScore(int englishScore) {
        this.englishScore = englishScore;
    }

    public int getEnglishRank() {
        return englishRank;
    }

    public void setEnglishRank(int englishRank) {
        this.englishRank = englishRank;
    }

    public int getPhysicsScore() {
        return physicsScore;
    }

    public void setPhysicsScore(int physicsScore) {
        this.physicsScore = physicsScore;
    }

    public int getPhysicsRank() {
        return physicsRank;
    }

    public void setPhysicsRank(int physicsRank) {
        this.physicsRank = physicsRank;
    }

    public int getChemistryScore() {
        return chemistryScore;
    }

    public void setChemistryScore(int chemistryScore) {
        this.chemistryScore = chemistryScore;
    }

    public int getChemistryRank() {
        return chemistryRank;
    }

    public void setChemistryRank(int chemistryRank) {
        this.chemistryRank = chemistryRank;
    }

    public int getBiologyScore() {
        return biologyScore;
    }

    public void setBiologyScore(int biologyScore) {
        this.biologyScore = biologyScore;
    }

    public int getBiologyRank() {
        return biologyRank;
    }

    public void setBiologyRank(int biologyRank) {
        this.biologyRank = biologyRank;
    }

    public int getHistoryScore() {
        return historyScore;
    }

    public void setHistoryScore(int historyScore) {
        this.historyScore = historyScore;
    }

    public int getHistoryRank() {
        return historyRank;
    }

    public void setHistoryRank(int historyRank) {
        this.historyRank = historyRank;
    }

    public int getGeographyScore() {
        return geographyScore;
    }

    public void setGeographyScore(int geographyScore) {
        this.geographyScore = geographyScore;
    }

    public int getGeographyRank() {
        return geographyRank;
    }

    public void setGeographyRank(int geographyRank) {
        this.geographyRank = geographyRank;
    }

    public int getPoliticsScore() {
        return politicsScore;
    }

    public void setPoliticsScore(int politicsScore) {
        this.politicsScore = politicsScore;
    }

    public int getPoliticsRank() {
        return politicsRank;
    }

    public void setPoliticsRank(int politicsRank) {
        this.politicsRank = politicsRank;
    }

    public int getArtsTotalScore() {
        return artsTotalScore;
    }

    public void setArtsTotalScore(int artsTotalScore) {
        this.artsTotalScore = artsTotalScore;
    }

    public int getArtsTotalRank() {
        return artsTotalRank;
    }

    public void setArtsTotalRank(int artsTotalRank) {
        this.artsTotalRank = artsTotalRank;
    }

    public int getScienceTotalScore() {
        return scienceTotalScore;
    }

    public void setScienceTotalScore(int scienceTotalScore) {
        this.scienceTotalScore = scienceTotalScore;
    }

    public int getScienceTotalRank() {
        return scienceTotalRank;
    }

    public void setScienceTotalRank(int scienceTotalRank) {
        this.scienceTotalRank = scienceTotalRank;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getTotalRank() {
        return totalRank;
    }

    public void setTotalRank(int totalRank) {
        this.totalRank = totalRank;
    }
}
