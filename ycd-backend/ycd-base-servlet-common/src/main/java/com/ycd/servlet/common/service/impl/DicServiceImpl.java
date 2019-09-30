package com.ycd.servlet.common.service.impl;


import com.ycd.common.entity.Dic;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.common.service.interfaces.DicService;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Stream;

public class DicServiceImpl extends AbstractServiceWithCreateEntity<Dic> implements DicService {

    private static final String SIMPLE_TYPE = "1";

    @Override
    @Transactional
    public Long saveDic(Dic dic) {
        if (SimpleUtil.isEmpty(dic.getDicType())) {
            dic.setDicType(SIMPLE_TYPE);
        }
        //检查key不能有重复
        Dic queryParam = new Dic();
        queryParam.setName(dic.getName());
        queryParam.setDicKey(dic.getDicKey());
        SimpleUtil.trueAndThrows(mapper.selectCount(queryParam) > 0, "key值已经存在");
        return save(dic);
    }

    @Override
    @Transactional
    public void updateDic(Dic dic) {
        SimpleUtil.assertNotEmpty(dic.getId(), "id不能为空");
        if (SimpleUtil.isNotEmpty(dic.getName()) && SimpleUtil.isNotEmpty(dic.getDicKey())) {
            //检查key不能有重复
            Example example = new Example(Dic.class);
            example.createCriteria().andEqualTo("dicKey", dic.getDicKey())
                    .andEqualTo("name", dic.getName())
                    .andNotEqualTo("id", dic.getId());
            SimpleUtil.trueAndThrows(mapper.selectCountByExample(example) > 0, "key值已经存在");
        }
        if (SimpleUtil.isNotEmpty(dic.getDicType()) && (!SIMPLE_TYPE.equals(dic.getDicType()))) {
            //不是简单类型，这三样禁止修改
            dic.setName(null);
            dic.setDicType(null);
            dic.setParentId(null);
        }
        update(dic);
    }

    @Override
    @Transactional
    public void deleteDicByIds(String ids) {
        SimpleUtil.assertNotEmpty(ids, "ids不能为空");
        Stream.of(ids.split(",")).map(Long::valueOf)
                .forEach(this::deleteById);
    }

    @Override
    public List<Dic> queryDicByName(String name) {
        Dic dic = new Dic();
        dic.setName(name);
        return mapper.select(dic);
    }
}
