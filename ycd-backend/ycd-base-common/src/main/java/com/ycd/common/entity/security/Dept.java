package com.ycd.common.entity.security;


import com.ycd.common.entity.AbstractEntity;
import com.ycd.common.util.SimpleUtil;
import com.ycd.common.util.TreeUtil;
import com.ycd.common.validation.Groups;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;


public class Dept extends AbstractEntity implements TreeUtil.TreeNode {


    /**
     * 上级id
     */
    @ApiModelProperty(name = "parentId", value = "父级部门id")
    private Long parentId;


    /**
     * 部门名称
     */
    @ApiModelProperty(name = "deptName", value = "部门名称")
    @NotEmpty(message = "组织的名称不能为空", groups = {Groups.Add.class, Groups.Update.class})
    @Length(max = 100, message = "组织名称最大长度不能超过${max}个字符", groups = {Groups.Add.class, Groups.Update.class})
    private String deptName;


    /**
     * 排序字段
     */
    @ApiModelProperty(name = "orderNum", value = "排序字段")
    private Long orderNum;

    @Transient
    private List<Dept> children = new ArrayList<>();


    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Long getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Long orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String nodeId() {
        return getId() + "";
    }

    @Override
    public String parentId() {
        return parentId + "";
    }

    @Override
    public List<Dept> children() {
        return children;
    }

    @Override
    public boolean leaf() {
        return SimpleUtil.isEmpty(children);
    }
}