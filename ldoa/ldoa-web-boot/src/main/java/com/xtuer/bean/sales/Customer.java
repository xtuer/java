package com.xtuer.bean.sales;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.xtuer.util.Utils;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.LinkedList;
import java.util.List;

/**
 * 客户
 */
@Getter
@Setter
@JsonIgnoreProperties({"contactsJson"})
public class Customer {
    /**
     * 客户 ID
     */
    private long customerId;

    /**
     * 客户编码: 唯一
     */
    @Excel(name = "编号")
    @NotBlank(message = "客户编号不能为空")
    private String customerSn;

    /**
     * 客户名称
     */
    @Excel(name = "名称")
    @NotBlank(message = "客户名称不能为空")
    private String name;

    /**
     * 行业
     */
    @Excel(name = "行业")
    private String business;

    /**
     * 区域
     */
    @Excel(name = "区域")
    private String region;

    /**
     * 分类
     */
    @Excel(name = "分类")
    private String type;

    /**
     * 电话
     */
    @Excel(name = "电话")
    private String phone;

    /**
     * 地址
     */
    private String address;

    /**
     * 负责人
     */
    private String owner;

    /**
     * 备注
     */
    private String remark;

    /**
     * 关系等级
     */
    @Excel(name = "关系等级")
    private String level;

    /**
     * 重要程度
     */
    @Excel(name = "重要程度")
    private String importance;

    /**
     * 状态
     */
    @Excel(name = "状态")
    private String status;

    /**
     * 客户联系人
     */
    private List<CustomerContact> contacts = new LinkedList<>();

    /**
     * 客户联系人 JSON
     */
    private String contactsJson;

    private String getContactsJson() {
        return Utils.toJson(contacts);
    }

    private void setContactsJson(String contactsJson) {new LinkedList<>();
        this.contacts = Utils.fromJson(contactsJson, new TypeReference<LinkedList<CustomerContact>>() {});
        this.contacts = this.contacts != null ? this.contacts : new LinkedList<>();
    }
}
