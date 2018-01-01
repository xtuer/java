var Urls = {
    REST_EMPTY: '/api/empty', // 啥都不返回，为了某些特殊情况使用

    // 试卷目录
    REST_PAPER_SUBDIRECTORIES     : '/rest/paperDirectories/{paperDirectoryId}/paperSubdirectories',
    REST_PAPER_DIRECTORIES        : '/rest/paperDirectories',
    REST_PAPER_DIRECTORIES_BY_ID  : '/rest/paperDirectories/{paperDirectoryId}',
    REST_PAPER_DIRECTORY_NAME     : '/rest/paperDirectories/{paperDirectoryId}/name',
    REST_PAPER_DIRECTORY_PARENT_ID: '/rest/paperDirectories/{paperDirectoryId}/parentPaperDirectoryId',
    REST_PAPER_DIRECTORIES_PAPER_COUNTS: '/rest/paperDirectories/paperCounts',
    REST_PAPER_DIRECTORIES_KNOWLEDGE_POINTS: '/rest/paperDirectories/{paperDirectoryId}/knowledgePoints',

    // 知识点分类
    REST_KNOWLEDGE_POINT_GROUPS      : '/rest/knowledgePointGroups',
    REST_KNOWLEDGE_POINT_GROUPS_BY_ID: '/rest/knowledgePointGroups/{knowledgePointGroupId}',

    // 知识点
    REST_KNOWLEDGE_POINTS_FILTER   : '/rest/knowledgePoints?parentKnowledgePointId={parentKnowledgePointId}&type={type}',
    REST_KNOWLEDGE_POINTS          : '/rest/knowledgePoints',
    REST_KNOWLEDGE_POINTS_BY_ID    : '/rest/knowledgePoints/{knowledgePointId}',
    REST_KNOWLEDGE_POINTS_IN_GROUP : '/rest/knowledgePointGroups/{knowledgePointGroupId}/knowledgePoints',
    REST_KNOWLEDGE_POINTS_NAME     : '/rest/knowledgePoints/{knowledgePointId}/name',
    REST_KNOWLEDGE_POINTS_PARENT_ID: '/rest/knowledgePoints/{knowledgePointId}/parentKnowledgePointId',

    // 试卷
    REST_PAPERS_BY_ID           : '/rest/papers/{paperId}',
    REST_PAPERS_SEARCH          : '/rest/papers/search',
    REST_PAPERS_OF_DIRECTORY    : '/rest/paperDirectories/{paperDirectoryId}/papers',
    REST_PAPERS_REDIRECTORY     : '/rest/papers/re-directory',
    REST_PAPERS_KNOWLEDGE_POINTS: '/rest/papers/{paperId}/knowledgePoints',
    REST_PAPERS_KNOWLEDGE_POINTS_BY_ID: '/rest/papers/{paperId}/knowledgePoints/{knowledgePointId}',
    REST_PAPERS_SEARCH_IN_DIRECTORY   : '/rest/paperDirectories/{paperDirectoryId}/papers/search',
    REST_PAPERS_COUNT_SEARCH_IN_DIRECTORY: '/rest/paperDirectories/{paperDirectoryId}/papers/countAsSearch',
    REST_PAPERS_PREVIEW               : '/rest/papers/{paperId}/preview',
    REST_PAPERS_DOWNLOAD              : '/rest/papers/{paperId}/download',
    REST_EXPORT_PAPERS                : '/rest/exportPapers',
    REST_EXPORT_PAPERS_STATUS         : '/rest/exportPapers/status',

    // 单题
    REST_QUESTION_KNOWLEDGE_POINTS                : '/rest/questionKnowledgePoints',
    REST_QUESTION_KNOWLEDGE_POINTS_BY_PARENT_ID   : '/rest/questionKnowledgePoints/{parentId}/children',
    REST_QUESTION_KNOWLEDGE_POINTS_BY_SUBJECT_CODE: '/rest/subjectCodes/{subjectCode}/questionKnowledgePoints',
    REST_QUESTIONS_UNDER_KNOWLEDGE_POINT          : '/rest/questionKnowledgePoints/{questionKnowledgePointId}/questions',
    REST_TOGGLE_QUESTION_MARK                     : '/rest/questions/{questionId}/toggleMark'
};
