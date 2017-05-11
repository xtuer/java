var Urls = {
    // 试卷目录
    REST_PAPER_SUBDIRECTORIES     : '/rest/paperDirectories/{paperDirectoryId}/paperSubdirectories',
    REST_PAPER_DIRECTORIES        : '/rest/paperDirectories',
    REST_PAPER_DIRECTORIES_BY_ID  : '/rest/paperDirectories/{paperDirectoryId}',
    REST_PAPER_DIRECTORY_NAME     : '/rest/paperDirectories/{paperDirectoryId}/name',
    REST_PAPER_DIRECTORY_PARENT_ID: '/rest/paperDirectories/{paperDirectoryId}/parentPaperDirectoryId',
    REST_PAPER_DIRECTORIES_PAPERS_COUNT: '/rest/paperDirectories/papersCount',
    REST_PAPER_DIRECTORIES_KNOWLEDGE_POINTS: '/rest/paperDirectories/{paperDirectoryId}/knowledgePoints',

    // 知识点分类
    REST_KNOWLEDGE_POINT_GROUPS      : '/rest/knowledgePointGroups',
    REST_KNOWLEDGE_POINT_GROUPS_BY_ID: '/rest/knowledgePointGroups/{knowledgePointGroupId}',

    // 知识点
    REST_KNOWLEDGE_POINTS_OF_GROUP: '/rest/knowledgePointGroups/{knowledgePointGroupId}/knowledgePoints',
    REST_KNOWLEDGE_POINTS         : '/rest/knowledgePoints',
    REST_KNOWLEDGE_POINTS_BY_ID   : '/rest/knowledgePoints/{knowledgePointId}',

    // 试卷
    REST_PAPERS_BY_ID           : '/rest/papers/{paperId}',
    REST_PAPERS_SEARCH          : '/rest/papers/search',
    REST_PAPERS_OF_DIRECTORY    : '/rest/paperDirectories/{paperDirectoryId}/papers',
    REST_PAPERS_REDIRECTORY     : '/rest/papers/re-directory',
    REST_PAPERS_KNOWLEDGE_POINTS: '/rest/papers/{paperId}/knowledgePoints',
    REST_PAPERS_KNOWLEDGE_POINTS_BY_ID: '/rest/papers/{paperId}/knowledgePoints/{knowledgePointId}',
    REST_PAPERS_SEARCH_IN_DIRECTORY   : '/rest/paperDirectories/{paperDirectoryId}/papers/search'
};
