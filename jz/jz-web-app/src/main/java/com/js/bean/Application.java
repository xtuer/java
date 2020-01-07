package com.js.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Application {
    private String name; // 姓名
    private String gender; // 性别
    private String birthday; // 出身日期
    private String idCardNo; // 身份证号码
    private String mobile; // 手机号码
    private boolean medicalBackground; // 医学背景
    private String educationLevel; // 文化程度
    private String graduationDate; // 毕业年限
    private String graduationMajor; // 毕业专业
    private String graduationSchool; // 毕业院校
    private String workUnit ; // 工作单位
    private int    workYear;  // 工作年限
    private String job; // 从事职业
    private String workUnitAddress; // 单位地址
    private String workExperience; // 工作经历

    private String picture; // 2 寸白底照片
    private String graduationCertificate; // 毕业证照片
    private String applicationForm; // 个人申请表
    private String idCardPictureFront; // 身份证正面照片
    private String idCardPictureBack; // 身份证反面照片
    private String workCertificate; // 工作证明
    private String commitmentLetter; // 承诺书
    private String xxwCertificate; // 学信网证明
}
