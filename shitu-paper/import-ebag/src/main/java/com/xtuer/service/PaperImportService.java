package com.xtuer.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xtuer.bean.KnowledgePoint;
import com.xtuer.bean.Paper;
import com.xtuer.bean.PaperDirectory;
import com.xtuer.mapper.ImporterMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Service
public class PaperImportService {
    private static Logger logger = LoggerFactory.getLogger(PaperImportService.class);

    @Resource(name="config")
    private Properties config;

    @Autowired
    private ImporterMapper mapper;

    private static final String PAPERS_FILE            = "papers.json"; // 试卷
    private static final String PAPER_DIRECTORIES_FILE = "paperDirectories.json"; // 试卷目录
    private static final String KNOWLEDGE_POINTS_FILE  = "knowledgePoints.json";  // 知识点
    private static final String PAPER_KNOWLEDGE_POINT_RELATION_FILE = "paperKnowledgePointRelation.json"; // 试卷知识点关系

    // 导入试卷
    @Transactional
    public void importPapers() throws IOException {
        logger.info("开始导入试卷");

        // 每个 paper 是一行 json
        String tenantCode = getTenantCode();
        List<String> jsons = FileUtils.readLines(new File(getPaperMetaDirectory(), PAPERS_FILE), "UTF-8");

        for (String json : jsons) {
            if (!StringUtils.isBlank(json)) {
                Paper paper = JSON.parseObject(json, Paper.class);
                paper.setTenantCode(tenantCode);
                mapper.insertPaper(paper);
            }
        }

        logger.info("结束导入试卷");
    }

    // 导入目录
    @Transactional
    public void importPaperDirectories() throws IOException {
        logger.info("开始导入目录");

        // 所有的目录都放在一个数组里
        String tenantCode = getTenantCode();
        String json = FileUtils.readFileToString(new File(getPaperMetaDirectory(), PAPER_DIRECTORIES_FILE), "UTF-8");
        List<PaperDirectory> directories = JSON.parseObject(json, new TypeReference<List<PaperDirectory>>(){});

        for (PaperDirectory directory : directories) {
            directory.setTenantCode(tenantCode);
            mapper.insertPaperDirectory(directory);
        }

        logger.info("结束导入目录");
    }

    // 导入知识点
    @Transactional
    public void importKnowledgePoints() throws IOException {
        logger.info("开始导入知识点");

        String tenantCode = getTenantCode();
        String json = FileUtils.readFileToString(new File(getPaperMetaDirectory(), KNOWLEDGE_POINTS_FILE), "UTF-8");
        List<KnowledgePoint> points = JSON.parseObject(json, new TypeReference<List<KnowledgePoint>>(){});

        for (KnowledgePoint point : points) {
            point.setTenantCode(tenantCode);
            mapper.insertKnowledgePoint(point);
        }

        logger.info("结束导入知识点");
    }

    // 导入试卷知识点关系
    @Transactional
    public void importPaperKnowledgePointRelation() throws IOException {
        logger.info("开始导入试卷知识点关系");

        String tenantCode = getTenantCode();
        String json = FileUtils.readFileToString(new File(getPaperMetaDirectory(), PAPER_KNOWLEDGE_POINT_RELATION_FILE), "UTF-8");
        List<KnowledgePoint> relations = JSON.parseObject(json, new TypeReference<List<KnowledgePoint>>(){});

        for (KnowledgePoint relation : relations) {
            relation.setTenantCode(tenantCode);
            mapper.insertPaperKnowledgePointRelation(relation);
        }

        logger.info("结束导入试卷知识点关系");
    }

    private String getPaperMetaDirectory() {
        return config.getProperty("paperMetaDirectory");
    }

    private String getTenantCode() {
        return config.get("tenantCode").toString(); // 如果是 int 的字符串，getProperty() 返回 null
    }
}
