package com.ycd.servlet.security.service.interfaces;


import com.ycd.common.entity.security.Dept;
import com.ycd.servlet.common.service.interfaces.LongPriService;

public interface DeptService<T extends Dept> extends LongPriService<T> {

    /**
     * 保存部门
     *
     * @param t 部门
     * @return 保存部门的id
     */
    Long saveDept(T t);

    /**
     * 修改部门
     *
     * @param t 部门
     */
    void updateDept(T t);

    /**
     * 根据部门的ids删除部门
     *
     * @param ids 部门ids
     */
    void deleteDeptByIds(String ids);
}
