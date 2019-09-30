package com.ycd.common.entity.security;



import com.ycd.common.entity.AbstractEntity;
import com.ycd.common.swagger.IgnoreSwaggerParameter;
import com.ycd.common.validation.Groups;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import java.util.List;


public class Role extends AbstractEntity {


    @NotEmpty(message = "角色名不能为空", groups = {Groups.Add.class, Groups.Update.class})
    @Length(max = 50, message = "名字最长为50个字符", groups = {Groups.Add.class, Groups.Update.class})
    @ApiModelProperty(name = "name", value = "角色的名字")
    private String name;

    @Length(max = 100, message = "备注不能超过${max}个字符", groups = {Groups.Add.class, Groups.Update.class})
    @ApiModelProperty(name = "name", value = "备注")
    private String remark;

    @Transient
    @IgnoreSwaggerParameter
    private List<Menu> menus;

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role() {
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }
}
