package com.ycd.servlet.security.service.interfaces;


import com.ycd.common.entity.security.Role;
import com.ycd.servlet.common.service.interfaces.LongPriService;

public interface RoleService<T extends Role> extends LongPriService<T> {

    /**
     * 保存角色
     *
     * @param t 角色
     * @return 角色id
     */
    Long saveRole(T t);

    /**
     * 修改角色
     *
     * @param t 角色
     */
    void updateRole(T t);

    /**
     * 通过角色的ids删除角色
     *
     * @param ids 角色ids
     */
    void deleteRolesByIds(String ids);
}
