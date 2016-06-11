package com.eduedu.ebag.controller;

public interface UriViewConstants {
    // 通用 URI 和 VIEW
    String FORWARD        = "forward:";
    String REDIRECT       = "redirect:";
    String URI_404        = "/404";
    String VIEW_404       = "404.htm";
    String SUCCESS        = "/success";
    String SUBMIT_SUCCESS = "/submit-success";

    // 登录注销
    String URI_LOGIN  = "/login";    // 登陆 URL
    String URI_LOGOUT = "/logout";   // 注销 URL
    String URI_DENY   = "/deny";     // 无权访问页面的 URL
    String VIEW_LOGIN = "login.htm"; // 登陆页面

    String URI_FILE_SYSTEM = "/filesystem";
    String VIEW_FILE_SYSTEM = "file-system.html";

    String REST_FILE_SYSTEM_FILES_WITH_ID = "/rest/filesystem/files/{fileId}";                           // 访问文件的 URL
    String REST_FILE_SYSTEM_FILES         = "/rest/filesystem/directories/{directoryId}/files";          // 访问目录下的文件和子文件夹的 URL
    String REST_FILE_SYSTEM_DIRECTORIES   = "/rest/filesystem/directories";                              // 访问目录的 URL
    String REST_FILE_SYSTEM_SUBDIRECTORIES= "/rest/filesystem/directories/{directoryId}/subdirectories"; // 访问目录下子文件夹的 URL
    String REST_FILE_SYSTEM_FILES_WITHOUT_SUBDIRECTORIES =" /rest/filesystem/directories/{directoryId}/files-without-subdirectories"; // 访问目录下的文件的 URL
}
