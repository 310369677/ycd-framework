package com.ycd.webflux.security.web;


import com.ycd.common.Result;
import com.ycd.common.anno.TimeCalculate;
import com.ycd.common.entity.security.Dept;
import com.ycd.common.exception.BusinessException;
import com.ycd.common.web.AbstractController;
import com.ycd.webflux.security.service.interfaces.ReactiveDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

/**
 * 描述:
 * 作者:杨川东
 * 日期:2019-06-02
 */

@RequestMapping("dept")
@Api(tags = "部门")
@TimeCalculate
public class ReactiveDeptController<T extends Dept> extends AbstractController {

    @Autowired
    protected ReactiveDeptService<T> reactiveDeptService;

    @ApiOperation(value = "添加部门")
    @PostMapping("save")
    @ResponseBody
    public Mono<Result<Long>> saveDept(T t) {
        return reactiveDeptService.saveDept(t).
                flatMap(id -> Mono.just(Result.ok(id)))
                .switchIfEmpty(Mono.error(new BusinessException("保存部门失败")));
    }


    @ApiOperation("修改部门")
    @PostMapping("update")
    @ResponseBody
    public Mono<Result<String>> updateDept(T t) {
        return reactiveDeptService.updateDept(t).map(vo -> Result.ok())
                .switchIfEmpty(Mono.just(Result.ok()));
    }


    @ApiOperation("根据ids删除部门")
    @ApiImplicitParam(name = "ids", value = "部门ids")
    @PostMapping("delete")
    @ResponseBody
    public Mono<Result<String>> deleteDeptByIds(String ids) {
        return reactiveDeptService.deleteDeptByIds(ids).map(t -> Result.ok())
                .defaultIfEmpty(Result.ok());
    }
}
