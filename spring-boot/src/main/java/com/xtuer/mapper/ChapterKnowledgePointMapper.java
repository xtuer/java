package com.xtuer.mapper;

import com.xtuer.bean.Chapter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChapterKnowledgePointMapper {
    /**
     * 查找章节的知识点
     *
     * @param bookCode 教材编码
     * @param chapterCode 章节编码
     * @return 返回章节知识点
     */
    Chapter findChapterKnowledgePointByChapterCode(String bookCode, String chapterCode);
}
