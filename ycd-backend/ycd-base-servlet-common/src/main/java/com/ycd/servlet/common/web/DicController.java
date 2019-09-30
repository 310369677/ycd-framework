package com.ycd.servlet.common.web;


import com.ycd.common.Result;
import com.ycd.common.dto.Combobox;
import com.ycd.common.entity.Dic;
import com.ycd.servlet.common.service.interfaces.DicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("dic")
@Api(tags = "字典")
public class DicController {

    @Autowired
    DicService dicService;

    @PostMapping("save")
    @ResponseBody
    @ApiOperation("新增")
    public Result<Long> save(Dic dic) {
        return Result.ok(dicService.saveDic(dic));
    }


    @PostMapping("update")
    @ResponseBody
    @ApiOperation("修改")
    public Result<String> update(Dic dic) {
        dicService.update(dic);
        return Result.ok();
    }

    @PostMapping("delete")
    @ResponseBody
    @ApiOperation("删除")
    @ApiImplicitParam(name = "ids", value = "字典的ids")
    public Result<String> delete(String ids) {
        dicService.deleteDicByIds(ids);
        return Result.ok();
    }


    @GetMapping("{name}")
    @ResponseBody
    @ApiOperation("根据名字查询字典")
    @ApiImplicitParam(name = "name", value = "名字", paramType = "path")
    @Cacheable("__dic__cache__")
    public List<Combobox> dicList(@PathVariable String name) {
        return dicService.queryDicByName(name).stream()
                .map(t -> {
                    Combobox combobox = new Combobox();
                    combobox.setId(t.getDicKey());
                    combobox.setParentId(t.getParentId());
                    combobox.setText(t.getDicValue());
                    return combobox;
                }).collect(Collectors.toList());
    }
}
