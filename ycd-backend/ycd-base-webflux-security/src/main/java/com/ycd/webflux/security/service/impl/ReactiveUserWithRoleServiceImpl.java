package com.ycd.webflux.security.service.impl;


import com.ycd.common.entity.security.UserWithRole;
import com.ycd.common.repo.mybatis.security.DefaultRoleMapper;
import com.ycd.common.repo.mybatis.security.DefaultUserMapper;
import com.ycd.common.repo.mybatis.security.UserRoleMapper;
import com.ycd.common.util.SimpleUtil;
import com.ycd.webflux.common.reactivetransaction.WebFluxTransactional;
import com.ycd.webflux.common.service.impl.AbstractReactiveServiceWithCreateEntity;
import com.ycd.webflux.security.service.interfaces.ReactiveUserWithRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactiveUserWithRoleServiceImpl extends AbstractReactiveServiceWithCreateEntity<UserWithRole> implements ReactiveUserWithRoleService<UserWithRole> {

    @Autowired
    UserRoleMapper userRoleMapper;

    @Autowired
    DefaultUserMapper userMapper;

    @Autowired
    DefaultRoleMapper roleMapper;


    @Override
    @WebFluxTransactional
    public Mono<Void> relationUserAndRole(String userId, String roleId) {
        return Mono.fromRunnable(() -> {
            if (SimpleUtil.isEmpty(userId) || SimpleUtil.isEmpty(roleId)) {
                return;
            }
            UserWithRole userWithRole = new UserWithRole();
            userWithRole.setRoleId(roleId);
            userWithRole.setUserId(userId);
            UserWithRole dataBase = userRoleMapper.selectOne(userWithRole);
            if (SimpleUtil.isNotEmpty(dataBase)) {
                return;
            }
            SimpleUtil.assertNotEmpty(userMapper.selectByPrimaryKey(Long.valueOf(userId)), "无效的用户id");
            SimpleUtil.assertNotEmpty(roleMapper.selectByPrimaryKey(Long.valueOf(roleId)), "无效的角色id");
            int count = userRoleMapper.insertSelective(userWithRole);
            SimpleUtil.trueAndThrows(count != 1, "关联用户和角色失败");
        });
    }

    @Override
    @WebFluxTransactional
    public Mono<Void> relationUserAndRoles(String userId, String roleIds) {
        if (SimpleUtil.isEmpty(userId) || SimpleUtil.isEmpty(roleIds)) {
            return Mono.empty();
        }
        String[] roleIdArray = roleIds.split(",");
        Flux<String> flux = Flux.fromArray(roleIdArray);
        return flux.flatMap(roleId -> relationUserAndRole(userId, roleId).map(Void -> true)
                .defaultIfEmpty(true)).reduce((t1, t2) -> t1).then();
    }
}
