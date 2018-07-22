package com.xtuer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;

@Controller
public class UploadController {
    @Autowired
    private ServletContext servletContext;

    /**
     * 上传单个文件的页面
     * @return 页面的路径
     */
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String uploadFilePage() {
        return "upload.html";
    }

    /**
     * 上传单个文件
     *
     * @param files 上传文件 MultipartFile 的对象
     * @return 上传的结果
     */
    @RequestMapping(value = "/uploads", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(@RequestParam("files") MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            System.out.println(file.getOriginalFilename());
            file.transferTo(new File("/Users/Biao/Desktop/upload/" + file.getOriginalFilename()));
        }

        return "Success";
    }

    /**
     * 上传单个文件
     *
     * @param file 上传文件 MultipartFile 的对象
     * @return 上传的结果
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam String username, @RequestParam String password) throws IOException {
        System.out.println(username + ", " + password);
        System.out.println(file.getOriginalFilename());
        file.transferTo(new File("/Users/Biao/Desktop/upload/" + file.getOriginalFilename()));

        return "Success";
    }
}
