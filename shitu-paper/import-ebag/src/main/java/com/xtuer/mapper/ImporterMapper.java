package com.xtuer.mapper;

import com.xtuer.bean.KnowledgePoint;
import com.xtuer.bean.Paper;
import com.xtuer.bean.PaperDirectory;

public interface ImporterMapper {
    void insertPaper(Paper paper);
    void insertPaperDirectory(PaperDirectory directory);
    void insertKnowledgePoint(KnowledgePoint point);
    void insertPaperKnowledgePointRelation(KnowledgePoint relation);
}
