package com.ycd.servlet.security.web;


import com.ycd.common.Result;
import com.ycd.common.anno.TimeCalculate;
import com.ycd.common.entity.security.Dept;
import com.ycd.common.web.AbstractController;
import com.ycd.servlet.security.service.interfaces.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("dept")
@Api(tags = "部门")
@TimeCalculate
public class DeptController<T extends Dept> extends AbstractController {


    @Autowired
    protected DeptService<T> deptService;

    @ApiOperation(value = "添加部门")
    @PostMapping("save")
    @ResponseBody
    public Result<Long> saveDept(T t) {
        return Result.ok(deptService.saveDept(t));
    }


    @ApiOperation("修改部门")
    @PostMapping("update")
    @ResponseBody
    public Result<String> updateDept(T t) {
        deptService.updateDept(t);
        return Result.ok();
    }

    @ApiOperation("根据ids删除部门")
    @PostMapping("delete")
    @ApiImplicitParam(name = "ids", value = "部门ids")
    @ResponseBody
    public Result<String> deleteDeptByIds(String ids) {
        deptService.deleteDeptByIds(ids);
        return Result.ok();
    }
}
