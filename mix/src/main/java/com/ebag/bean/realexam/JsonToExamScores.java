package com.ebag.bean.realexam;

import com.alibaba.fastjson.JSON;

import java.io.FileInputStream;

public class JsonToExamScores {
    public static void main(String[] args) throws Exception {
        ExamScores scores = JSON.parseObject(new FileInputStream("/Users/Biao/Desktop/exam-scores.json"), ExamScores.class);
        System.out.println(JsonUtil.toJson(scores)); // 输出为 json 格式
    }
}
