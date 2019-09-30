package com.ycd.common.entity;


import com.ycd.common.entity.security.Role;
import com.ycd.common.swagger.IgnoreSwaggerParameter;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class User extends AbstractEntity {

    /**
     * 用户名
     */
    @NotEmpty(message = "用户名不能为空")
    @Length(max = 30, message = "用户名不能超过${max}个字符")
    @ApiModelProperty(name = "userName", value = "用户名")
    private String userName;

    /**
     * 昵称
     */
    @ApiModelProperty(name = "nick", value = "昵称")
    private String nick;


    @ApiModelProperty(name = "password", value = "密码")
    @NotEmpty(message = "密码不能为空")
    @Length(max = 100, message = "密码不能超过${max}个字符")
    private String password;

    @IgnoreSwaggerParameter
    private List<Role> roleList;


    /**
     * 状态
     */
    @ApiModelProperty(hidden = true)
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}