package com.js.bean;

public interface Urls {
    // 上传文件、图片到临时目录，仓库中的文件为正式的文件
    String FORM_UPLOAD_TEMP_FILE  = "/form/upload/temp/file";  // 上传一个临时文件
    String FORM_UPLOAD_TEMP_FILES = "/form/upload/temp/files"; // 上传多个临时文件
    String URL_TEMP_FILE_PREFIX   = "/file/temp/";             // 临时文件的 URL 前缀
    String URL_TEMP_FILE          = "/file/temp/{filename}";   // 临时文件的 URL
    String URL_REPO_FILE_PREFIX   = "/file/repo/";             // 仓库文件的 URL 前缀
    String URL_REPO_FILE          = "/file/repo/**";           // 仓库文件的 URL
    String URL_REPO_FILE_DOWNLOAD = "/file/download/**";       // 下载仓库文件的 URL

    String API_APPLICATION = "/api/application"; // 申请表
}
