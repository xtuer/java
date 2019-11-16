package com.exam.bean;

/**
 * 缓存使用的键和缓存的名字
 */
public interface CacheConst {
    String CACHE = "exam:"; // 默认的缓存对象

    String KEY_USER_ID = "'user.' + #userId";
    String KEY_ORG     = "'org.' + #host";
    String KEY_EXAM    = "'exam.' + #exam.id";
    String KEY_EXAM_ID = "'exam.' + #examId";
    String KEY_PAPER    = "'paper.' + #paper.id";
    String KEY_PAPER_ID = "'paper.' + #paperId";
}
