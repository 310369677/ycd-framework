package com.ycd.webflux.security.web;


import com.ycd.common.Result;
import com.ycd.common.anno.TimeCalculate;
import com.ycd.common.entity.security.Menu;
import com.ycd.common.validation.Groups;
import com.ycd.common.web.AbstractController;
import com.ycd.webflux.security.service.interfaces.ReactiveMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

@RequestMapping("menu")
@Api(tags = "菜单")
@TimeCalculate
public class ReactiveMenuController<T extends Menu> extends AbstractController {

    @Autowired
    protected ReactiveMenuService<T> reactiveMenuService;


    @ApiOperation("菜单新增")
    @PostMapping("add")
    @ResponseBody
    public Mono<Result<Long>> saveMenu(@Validated(Groups.Add.class) T menu) {
        return reactiveMenuService.saveMenu(menu).map(Result::ok);
    }

    @ApiOperation("菜单修改")
    @PostMapping("update")
    @ResponseBody
    public Mono<Result<String>> updateMenu(@Validated(Groups.Update.class) T menu) {
        return reactiveMenuService.updateMenu(menu).map(Void -> Result.ok())
                .defaultIfEmpty(Result.ok());
    }


    @ApiOperation("菜单删除")
    @ApiImplicitParam(name = "menuIds", value = "菜单ids")
    @PostMapping("delete")
    @ResponseBody
    public Mono<Result<String>> deleteMenu(String menuIds) {
        return reactiveMenuService.deleteMenu(menuIds).map(Void -> Result.ok())
                .defaultIfEmpty(Result.ok());
    }
}
