package com.ycd.webflux.security.web;


import com.ycd.common.Result;
import com.ycd.common.anno.TimeCalculate;
import com.ycd.common.entity.User;
import com.ycd.common.web.AbstractController;
import com.ycd.webflux.security.service.interfaces.ReactiveUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

/**
 * 描述:
 * 作者:杨川东
 * 日期:2019-05-31
 */
@RequestMapping("user")
@Api(tags = "用户")
@TimeCalculate
public class ReactiveUserController<T extends User> extends AbstractController {

    @Autowired
    protected ReactiveUserService<T> reactiveUserService;

    @ApiOperation("新增用户")
    @PostMapping("add")
    @ResponseBody
    public Mono<Result<Long>> saveUser(T t) {
        return reactiveUserService.saveUser(t).flatMap(id -> Mono.just(Result.ok(id)));

    }

    @ApiOperation("删除用户")
    @ApiImplicitParam(name = "ids", value = "用户的ids")
    @PostMapping("delete")
    @ResponseBody
    public Mono<Result<String>> deleteUser(String ids) {
        return reactiveUserService.deleteUser(ids).map(vo -> Result.ok())
                .defaultIfEmpty(Result.ok());

    }

    @ApiOperation("改变用户状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "用户ids"),
            @ApiImplicitParam(name = "status", value = "用户状态")
    })
    @PostMapping("changeStatus")
    @ResponseBody
    public Mono<Result<String>> changeUserStatus(String ids, String status) {
        return reactiveUserService.changeUserStatus(ids, status).map(vo -> Result.ok())
                .switchIfEmpty(Mono.just(Result.ok()));
    }


}
