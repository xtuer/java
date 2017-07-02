package com.xtuer.mapper;

import com.xtuer.bean.Paper;

public interface ImporterMapper {
    void insertPaper(Paper paper);
    void updatePaperMeta(Paper paper);
}
