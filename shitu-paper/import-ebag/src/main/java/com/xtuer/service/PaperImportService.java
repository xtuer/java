package com.xtuer.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xtuer.bean.KnowledgePoint;
import com.xtuer.bean.Paper;
import com.xtuer.bean.PaperDirectory;
import com.xtuer.mapper.ImporterMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

@Service
public class PaperImportService {
    @Resource(name="config")
    private Properties config;

    @Autowired
    private ImporterMapper mapper;

    private static final String PAPERS_FILE = "papers.json"; // 试卷
    private static final String PAPER_DIRECTORIES_FILE = "paperDirectories.json"; // 试卷目录
    private static final String KNOWLEDGE_POINTS_FILE = "knowledgePoints.json"; // 知识点
    private static final String PAPER_KNOWLEDGE_POINT_RELATION_FILE = "paperKnowledgePointRelation.json"; // 试卷知识点关系

    // 导入试卷
    @Transactional
    public void importPapers() throws IOException {
        // 每个 paper 是一行 json
        List<String> jsons = FileUtils.readLines(new File(getPaperMetaDirectory(), PAPERS_FILE));

        for (String json : jsons) {
            if (!StringUtils.isBlank(json)) {
                Paper paper = JSON.parseObject(json, Paper.class);
                mapper.insertPaper(paper);
            }
        }
    }

    // 导入目录
    @Transactional
    public void importPaperDirectories() throws IOException {
        // 所有的目录都放在一个数组里
        String json = FileUtils.readFileToString(new File(getPaperMetaDirectory(), PAPER_DIRECTORIES_FILE));
        List<PaperDirectory> directories = JSON.parseObject(json, new TypeReference<List<PaperDirectory>>(){});

        for (PaperDirectory directory : directories) {
            mapper.insertPaperDirectory(directory);
        }
    }

    // 导入知识点
    @Transactional
    public void importKnowledgePoints() throws IOException {
        String json = FileUtils.readFileToString(new File(getPaperMetaDirectory(), KNOWLEDGE_POINTS_FILE));
        List<KnowledgePoint> points = JSON.parseObject(json, new TypeReference<List<KnowledgePoint>>(){});

        for (KnowledgePoint point : points) {
            mapper.insertKnowledgePoint(point);
        }
    }

    // 导入试卷知识点关系
    @Transactional
    public void importPaperKnowledgePointRelation() throws IOException {
        String json = FileUtils.readFileToString(new File(getPaperMetaDirectory(), PAPER_KNOWLEDGE_POINT_RELATION_FILE));
        List<KnowledgePoint> relations = JSON.parseObject(json, new TypeReference<List<KnowledgePoint>>(){});

        for (KnowledgePoint relation : relations) {
            mapper.insertPaperKnowledgePointRelation(relation);
        }
    }

    private String getPaperMetaDirectory() {
        return config.getProperty("paperMetaDirectory");
    }
}
