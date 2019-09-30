package com.ycd.servlet.security.web;


import com.ycd.common.Result;
import com.ycd.common.anno.TimeCalculate;
import com.ycd.common.entity.User;
import com.ycd.common.web.AbstractController;
import com.ycd.servlet.security.service.interfaces.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("user")
@Api(tags = "用户")
@TimeCalculate
public class UserController<T extends User> extends AbstractController {

    @Autowired
    UserService<T> userService;

    @ApiOperation("新增用户")
    @PostMapping("add")
    @ResponseBody
    public Result<Long> saveUser(T t) {
        return Result.ok(userService.saveUser(t));
    }

    @ApiOperation("删除用户")
    @PostMapping("delete")
    @ResponseBody
    @ApiImplicitParam(name = "ids", value = "用户的ids")
    public Result<String> deleteUser(String ids) {
        userService.deleteUser(ids);
        return Result.ok();

    }

    @ApiOperation("改变用户状态")
    @PostMapping("changeStatus")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "用户ids"),
            @ApiImplicitParam(name = "status", value = "用户状态")
    })
    public Result<String> changeUserStatus(String ids, String status) {
        userService.changeUserStatus(ids, status);
        return Result.ok();
    }
}
