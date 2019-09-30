package com.ycd.webflux.security.web;


import com.ycd.common.Result;
import com.ycd.common.anno.TimeCalculate;
import com.ycd.common.entity.security.Role;
import com.ycd.common.web.AbstractController;
import com.ycd.webflux.security.service.interfaces.ReactiveRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

@RequestMapping("role")
@Api(tags = "角色")
@TimeCalculate
public class ReactiveRoleController<T extends Role> extends AbstractController {

    @Autowired
    protected ReactiveRoleService<T> reactiveRoleService;


    @ApiOperation("添加角色")
    @PostMapping("add")
    @ResponseBody
    public Mono<Result<Long>> saveRole(T t) {
        Mono<Long> idMono = reactiveRoleService.saveRole(t);
        return idMono.map(Result::ok);
    }


    @ApiOperation("修改角色")
    @PostMapping("update")
    @ResponseBody
    public Mono<Result<String>> updateRole(T t) {
        return reactiveRoleService.updateRole(t).map(Void -> Result.ok())
                .defaultIfEmpty(Result.ok());
    }


    @ApiOperation("删除角色")
    @ApiImplicitParam(name = "ids", value = "角色的ids")
    @PostMapping("delete")
    @ResponseBody
    public Mono<Result<String>> deleteRolesByIds(String ids) {
        return reactiveRoleService.deleteRolesByIds(ids)
                .map(Void -> Result.ok())
                .defaultIfEmpty(Result.ok());
    }

}
