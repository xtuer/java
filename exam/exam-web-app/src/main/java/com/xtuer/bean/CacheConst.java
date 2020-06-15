package com.xtuer.bean;

/**
 * 缓存使用的键和缓存的名字
 */
public interface CacheConst {
    String CACHE = "xtuer:"; // 默认的缓存对象

    // 用户机构
    String KEY_USER_ID  = "'user.' + #userId";
    String KEY_ORG_HOST = "'org.' + #host";

    // 考试
    String KEY_EXAM     = "'exam.' + #exam.id";
    String KEY_EXAM_ID  = "'exam.' + #examId";
    String KEY_PAPER    = "'paper.' + #paper.id";
    String KEY_PAPER_ID = "'paper.' + #paperId";
}
