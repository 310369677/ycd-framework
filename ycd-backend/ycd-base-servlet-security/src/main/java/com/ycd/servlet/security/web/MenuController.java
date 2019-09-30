package com.ycd.servlet.security.web;


import com.ycd.common.Result;
import com.ycd.common.anno.TimeCalculate;
import com.ycd.common.entity.security.Menu;
import com.ycd.common.validation.Groups;
import com.ycd.common.web.AbstractController;
import com.ycd.servlet.security.service.interfaces.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("menu")
@Api(tags = "菜单")
@TimeCalculate
public class MenuController<T extends Menu> extends AbstractController {

    @Autowired
    MenuService<T> menuService;


    @ApiOperation("菜单新增")
    @PostMapping("add")
    @ResponseBody
    public Result<Long> saveMenu(@Validated(Groups.Add.class) T menu) {
        return Result.ok(menuService.saveMenu(menu));
    }

    @ApiOperation("菜单修改")
    @PostMapping("update")
    @ResponseBody
    public Result<String> updateMenu(@Validated(Groups.Update.class) T menu) {
        menuService.updateMenu(menu);
        return Result.ok();
    }


    @ApiOperation("菜单删除")
    @ApiImplicitParam(name = "menuIds", value = "菜单ids")
    @PostMapping("delete")
    @ResponseBody
    public Result<String> deleteMenu(String menuIds) {
        menuService.deleteMenu(menuIds);
        return Result.ok();
    }
}
