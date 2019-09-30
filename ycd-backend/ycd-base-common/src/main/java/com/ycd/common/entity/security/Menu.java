package com.ycd.common.entity.security;



import com.ycd.common.entity.AbstractEntity;
import com.ycd.common.util.SimpleUtil;
import com.ycd.common.util.TreeUtil;
import com.ycd.common.validation.Groups;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Table(name = "t_menu")
public class Menu extends AbstractEntity implements TreeUtil.TreeNode {


    public static final String TYPE_MENU = "0";

    public static final String TYPE_BUTTON = "1";


    /**
     * 父级id
     */
    @ApiModelProperty(name = "parentId", value = "菜单父级id")
    private Long parentId;


    /**
     * 菜单名字
     */
    @NotEmpty(message = "name不能为空", groups = {Groups.Add.class, Groups.Update.class})
    @Length(max = 50, message = "菜单名字不能超过${max}个字符")
    @ApiModelProperty(name = "name", value = "菜单名字")
    private String name;

    /**
     * url
     */
    @Length(max = 100, message = "url不能超过${max}个字符", groups = {Groups.Add.class, Groups.Update.class})
    @ApiModelProperty(name = "url", value = "菜单url")
    private String url;


    /**
     * 权限标识
     */
    @Length(max = 50, message = "权限标识不能超过${max}个字符", groups = {Groups.Add.class, Groups.Update.class})
    @ApiModelProperty(name = "perms", value = "权限标识")
    private String perms;


    /**
     * 小图标
     */
    @ApiModelProperty(name = "icon", value = "小图标")
    private String icon;


    /**
     * 菜单的类型，菜单或者按钮
     */
    @NotEmpty(message = "type不能为空", groups = {Groups.Add.class})
    @ApiModelProperty(name = "type", value = "菜单的类型，菜单或者按钮")
    private String type;


    /**
     * 排序字段
     */
    @ApiModelProperty(name = "orderNum", value = "排序字段")
    private Long orderNum;


    @Transient
    private List<Menu> children = new ArrayList<>();


    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Long orderNum) {
        this.orderNum = orderNum;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public void setChildren(List<Menu> children) {
        this.children = children;
    }

    @Override
    public String nodeId() {
        return getId() + "";
    }

    @Override
    public String parentId() {
        return getParentId() + "";
    }

    @Override
    public List<Menu> children() {
        return getChildren();
    }

    @Override
    public boolean leaf() {
        return SimpleUtil.isEmpty(getChildren());
    }
}