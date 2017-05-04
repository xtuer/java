var Urls = {
    // 试卷目录
    REST_PAPER_SUBDIRECTORIES      : '/rest/paperDirectories/{paperDirectoryId}/paperSubdirectories',
    REST_PAPER_DIRECTORIES         : '/rest/paperDirectories',
    REST_PAPER_DIRECTORIES_BY_ID   : '/rest/paperDirectories/{paperDirectoryId}',
    REST_PAPER_DIRECTORY_NAME      : '/rest/paperDirectories/{paperDirectoryId}/name',
    REST_PAPER_DIRECTORY_PARENT_ID : '/rest/paperDirectories/{paperDirectoryId}/parentPaperDirectoryId',

    // 知识点分类
    REST_KNOWLEDGE_POINT_GROUPS       : '/rest/knowledgePointGroups',
    REST_KNOWLEDGE_POINT_GROUPS_BY_ID : '/rest/knowledgePointGroups/{knowledgePointGroupId}',

    // 知识点
    REST_KNOWLEDGE_POINTS_OF_GROUP : '/rest/knowledgePointGroups/{knowledgePointGroupId}/knowledgePoints',
    REST_KNOWLEDGE_POINTS          : '/rest/knowledgePoints',
    REST_KNOWLEDGE_POINTS_BY_ID    : '/rest/knowledgePoints/{knowledgePointId}',

    // 试卷
    REST_PAPERS_SEARCH : '/rest/papers/search'
};
