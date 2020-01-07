package com.js.controller;

import com.js.bean.Application;
import com.js.bean.Result;
import com.js.bean.Urls;
import com.js.service.RepoFileService;
import com.js.service.TempFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
@Slf4j
public class XController {
    @Autowired
    private TempFileService tempFileService;

    @Autowired
    private RepoFileService repoFileService;

    @GetMapping("/api/demo")
    public Result<String> demo() {
        return Result.ok();
    }

    /**
     * 提交报名表单
     *
     * @param application 表单
     */
    @PostMapping(Urls.API_APPLICATION)
    public Result<String> submitApplication(@RequestBody Application application) throws IOException {
        String dir = application.getName();

        // 保存照片
        repoFileService.moveTempFileToRepo(application.getPicture(), "2 寸白底照片", dir);
        repoFileService.moveTempFileToRepo(application.getGraduationCertificate(), "毕业证照片", dir);
        repoFileService.moveTempFileToRepo(application.getApplicationForm(), "个人申请表", dir);
        repoFileService.moveTempFileToRepo(application.getIdCardPictureFront(), "身份证正面照片", dir);
        repoFileService.moveTempFileToRepo(application.getIdCardPictureBack(), "身份证反面照片", dir);
        repoFileService.moveTempFileToRepo(application.getWorkCertificate(), "工作证明", dir);
        repoFileService.moveTempFileToRepo(application.getCommitmentLetter(), "承诺书", dir);
        repoFileService.moveTempFileToRepo(application.getXxwCertificate(), "学信网证明", dir);

        // 保存信息
        StringBuilder sb = new StringBuilder();
        sb.append("姓名: ").append(application.getName()).append("\n")
                .append("性别:").append(application.getGender()).append("\n")
                .append("出身日期:").append(application.getBirthday()).append("\n")
                .append("身份证号码:").append(application.getIdCardNo()).append("\n")
                .append("手机号码:").append(application.getMobile()).append("\n")
                .append("医学背景:").append(application.isMedicalBackground() ? "有" : "没有").append("\n")
                .append("文化程度:").append(application.getEducationLevel()).append("\n")
                .append("毕业年限:").append(application.getGraduationDate()).append("\n")
                .append("毕业专业:").append(application.getGraduationMajor()).append("\n")
                .append("毕业院校:").append(application.getGraduationSchool()).append("\n")
                .append("工作单位:").append(application.getWorkUnit()).append("\n")
                .append("工作年限:").append(application.getWorkYear()).append("\n")
                .append("从事职业:").append(application.getJob()).append("\n")
                .append("单位地址:").append(application.getWorkUnitAddress()).append("\n")
                .append("工作经历:").append(application.getWorkExperience()).append("\n");

        File dataFile = repoFileService.getRepoFile(dir + "/报名数据.txt");
        FileUtils.writeStringToFile(dataFile, sb.toString(), "UTF-8");

        return Result.ok();
    }
}
