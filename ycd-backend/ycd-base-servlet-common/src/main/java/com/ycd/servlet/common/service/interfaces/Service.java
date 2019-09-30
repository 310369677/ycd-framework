package com.ycd.servlet.common.service.interfaces;

import java.util.List;

public interface Service<ID, T> {

    ID save(T t);

    List<ID> saveList(List<T> list);

    T update(T t);

    List<T> updateList(List<T> list);

    void deleteById(ID id);

    void deleteByIds(List<ID> ids);

    T findById(ID id);
}
