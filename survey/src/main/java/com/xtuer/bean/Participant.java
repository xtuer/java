package com.xtuer.bean;

import com.xtuer.util.RegularExpressionPattern;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

public class Participant {
    private int id;
    private String name;
    private boolean gender; // 性别, true 为 男, false 为女
    private String telephone;
    private String mail;

    public Participant() {
    }

    public Participant(int id, String name, boolean gender, String telephone, String mail) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.telephone = telephone;
        this.mail = mail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotBlank(message="用户名不能为空") // 进行参数验证
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    @Pattern(regexp= RegularExpressionPattern.TELEPHONE, message = "请输入正确的电话号码，如：0591-6487256，15005059587")
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Pattern(regexp= RegularExpressionPattern.MAIL, message = "请输入正确的邮件地址，如：alice@edu-edu.com.cn")
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", telephone='" + telephone + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }
}
