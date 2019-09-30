package com.ycd.webflux.security.service.interfaces;


import com.ycd.common.entity.security.Dept;
import com.ycd.webflux.common.service.interfaces.LongPriReactiveService;
import reactor.core.publisher.Mono;

/**
 * 描述:
 * 作者:杨川东
 * 日期:2019-06-02
 */
public interface ReactiveDeptService<T extends Dept> extends LongPriReactiveService<T> {

    /**
     * 保存部门
     *
     * @param t
     * @return
     */
    Mono<Long> saveDept(T t);

    Mono<Void> updateDept(T t);

    Mono<Void> deleteDeptByIds(String ids);
}
