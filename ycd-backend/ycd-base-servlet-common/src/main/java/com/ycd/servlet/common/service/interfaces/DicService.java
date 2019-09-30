package com.ycd.servlet.common.service.interfaces;


import com.ycd.common.entity.Dic;

import java.util.List;

public interface DicService extends LongPriService<Dic> {

    Long saveDic(Dic dic);

    void updateDic(Dic dic);

    void deleteDicByIds(String ids);

    List<Dic> queryDicByName(String name);
}
