package com.ycd.webflux.common.web;


import com.ycd.common.Result;
import com.ycd.common.dto.Combobox;
import com.ycd.common.entity.Dic;
import com.ycd.common.util.SimpleUtil;
import com.ycd.common.web.AbstractController;
import com.ycd.webflux.common.service.interfaces.ReactiveDicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("dic")
@Api(tags = "字典")
public class ReactiveDicController extends AbstractController {

    @Autowired
    private ReactiveDicService dicService;

    @PostMapping("save")
    @ResponseBody
    @ApiOperation("新增")
    public Mono<Result<Long>> save(Dic dic) {
        return dicService.saveDic(dic)
                .map(Result::ok)
                .switchIfEmpty(Mono.error(SimpleUtil.newBusinessException("保存字典失败")));
    }


    @PostMapping("update")
    @ResponseBody
    @ApiOperation("修改")
    public Mono<Result<String>> update(Dic dic) {
        return dicService.updateDic(dic)
                .map(Void -> Result.ok()).defaultIfEmpty(Result.ok());
    }

    @PostMapping("delete")
    @ResponseBody
    @ApiOperation("删除")
    @ApiImplicitParam(name = "ids", value = "字典的ids")
    public Mono<Result<String>> delete(String ids) {
        return dicService.deleteDicByIds(ids)
                .map(Void -> Result.ok()).defaultIfEmpty(Result.ok());
    }


    @GetMapping("{name}")
    @ResponseBody
    @ApiOperation("根据名字查询字典")
    @ApiImplicitParam(name = "name", value = "名字", paramType = "path")
    public Mono<List<Combobox>> dicList(@PathVariable String name) {

        return dicService.queryDicByName(name)
                .reduceWith(() -> (List<Combobox>) new ArrayList<Combobox>(), (t1, t2) -> {
                    Combobox combobox = new Combobox();
                    combobox.setId(t2.getDicKey());
                    combobox.setParentId(t2.getParentId());
                    combobox.setText(t2.getDicValue());
                    t1.add(combobox);
                    return t1;
                }).defaultIfEmpty(new ArrayList<>());
    }
}
