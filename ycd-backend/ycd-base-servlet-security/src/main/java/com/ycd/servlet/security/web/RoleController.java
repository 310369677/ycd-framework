package com.ycd.servlet.security.web;


import com.ycd.common.Result;
import com.ycd.common.anno.TimeCalculate;
import com.ycd.common.entity.security.Role;
import com.ycd.common.web.AbstractController;
import com.ycd.servlet.security.service.interfaces.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("role")
@Api(tags = "角色")
@TimeCalculate
public class RoleController<T extends Role> extends AbstractController {

    @Autowired
    RoleService<T> roleService;


    @ApiOperation("添加角色")
    @PostMapping("add")
    @ResponseBody
    public Result<Long> saveRole(T t) {
        return Result.ok(roleService.saveRole(t));
    }


    @ApiOperation("修改角色")
    @PostMapping("update")
    @ResponseBody
    public Result<String> updateRole(T t) {
        roleService.updateRole(t);
        return Result.ok();
    }


    @ApiOperation("删除角色")
    @ApiImplicitParam(name = "ids", value = "角色的ids")
    @PostMapping("delete")
    @ResponseBody
    public Result<String> deleteRolesByIds(String ids) {
        roleService.deleteRolesByIds(ids);
        return Result.ok();
    }
}
