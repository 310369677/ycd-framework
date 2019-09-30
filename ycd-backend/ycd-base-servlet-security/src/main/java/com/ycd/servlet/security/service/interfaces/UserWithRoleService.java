package com.ycd.servlet.security.service.interfaces;


import com.ycd.common.entity.security.UserWithRole;
import com.ycd.servlet.common.service.interfaces.LongPriService;

public interface UserWithRoleService<T extends UserWithRole> extends LongPriService<T> {

    void relationUserAndRole(String userId, String roleId);

    void relationUserAndRoles(String userId, String roleIds);

}
