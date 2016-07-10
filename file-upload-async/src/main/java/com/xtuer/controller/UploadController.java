package com.xtuer.controller;

import com.xtuer.bean.Result;
import com.xtuer.service.HandleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Controller
public class UploadController {
    private static Logger logger = LoggerFactory.getLogger(UploadController.class);
    @Autowired
    private HandleService handleService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * Ajax 上传文件的页面
     *
     * @return 页面的路径
     */
    @RequestMapping(value = "/ajax-upload-files", method = RequestMethod.GET)
    public String ajaxUploadFilesPage() {
        return "ajax-upload-files.html";
    }

    /**
     *
     * @param files 上传文件 MultipartFile 的对象数组
     */
    @RequestMapping(value="/ajax-upload-files", method = RequestMethod.POST)
    @ResponseBody
    public List<Result> ajaxUploadFiles(@RequestParam("files") MultipartFile[] files) {
        List<Result> results = new LinkedList<Result>();

        for (MultipartFile file : files) {
            String path = saveFile(file);

            if (path != null) {
                results.add(new Result(true, ""));
                System.out.println(Thread.currentThread().toString());
                handleService.handle(path); // 使用异步的方式处理文件
            } else {
                results.add(new Result(false, "上传失败"));
            }
        }

        return results;
    }

    @RequestMapping(value = "/ajax-upload-files-status", method = RequestMethod.GET)
    @ResponseBody
    public Result requestProgress() {
        String status = redisTemplate.opsForValue().get("uploadStatus");
        return new Result(true, status, "Redis data");
    }

    /**
     * 把 HTTP 请求中的文件流保存到本地
     *
     * @param file MultipartFile 的对象
     * @return 返回保存文件的路径, 如果返回 null, 则保存不成功
     */
    private String saveFile(MultipartFile file) {
        String path = null;

        if (!file.isEmpty()) {
            try {
                String uuid = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
                path = "/Users/Biao/Desktop/" + uuid + "-" + file.getOriginalFilename(); // 保存文件的路径
                FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(path));

                return path;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return path;
    }
}
