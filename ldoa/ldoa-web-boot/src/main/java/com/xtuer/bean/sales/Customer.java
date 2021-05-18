package com.xtuer.bean.sales;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
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
    @Excel(name = "编号", width = 20, needMerge = true)
    @NotBlank(message = "客户编号不能为空")
    private String customerSn;

    /**
     * 客户名称
     */
    @Excel(name = "名称", width = 30, needMerge = true)
    @NotBlank(message = "客户名称不能为空")
    private String name;

    /**
     * 行业
     */
    @Excel(name = "行业", width = 10, needMerge = true)
    private String business;

    /**
     * 区域
     */
    @Excel(name = "区域", width = 10, needMerge = true)
    private String region;

    /**
     * 分类
     */
    @Excel(name = "分类", isColumnHidden = true)
    private String type;

    /**
     * 电话
     */
    @Excel(name = "电话", width = 20, needMerge = true)
    private String phone;

    /**
     * 地址
     */
    @Excel(name = "地址", width = 40, needMerge = true)
    private String address;

    /**
     * 负责人
     */
    @Excel(name = "负责人", width = 10, needMerge = true)
    private String owner;

    /**
     * 备注
     */
    @Excel(name = "备注", width = 50, needMerge = true)
    private String remark;

    /**
     * 关系等级
     */
    @Excel(name = "关系等级", isColumnHidden = true, needMerge = true)
    private String level;

    /**
     * 重要程度
     */
    @Excel(name = "重要程度", isColumnHidden = true, needMerge = true)
    private String importance;

    /**
     * 状态
     */
    @Excel(name = "状态", isColumnHidden = true, needMerge = true)
    private String status;

    /**
     * 客户联系人
     */
    @ExcelCollection(name = "联系人")
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
