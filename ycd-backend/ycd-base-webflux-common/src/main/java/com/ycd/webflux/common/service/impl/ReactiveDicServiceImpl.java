package com.ycd.webflux.common.service.impl;


import com.ycd.common.entity.Dic;
import com.ycd.common.util.SimpleUtil;
import com.ycd.webflux.common.reactivetransaction.WebFluxTransactional;
import com.ycd.webflux.common.service.interfaces.ReactiveDicService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tk.mybatis.mapper.entity.Example;

public class ReactiveDicServiceImpl extends AbstractReactiveServiceWithCreateEntity<Dic> implements ReactiveDicService {

    private static final String SIMPLE_TYPE = "1";

    @Override
    @WebFluxTransactional
    public Mono<Long> saveDic(Dic dic) {
        return Mono.defer(() -> {
            if (SimpleUtil.isEmpty(dic.getDicType())) {
                dic.setDicType(SIMPLE_TYPE);
            }
            //检查key不能有重复
            Dic queryParam = new Dic();
            queryParam.setName(dic.getName());
            queryParam.setDicKey(dic.getDicKey());
            SimpleUtil.trueAndThrows(mapper.selectCount(queryParam) > 0, "key值已经存在");
            return save(dic);
        });
    }

    @Override
    @WebFluxTransactional
    public Mono<Void> updateDic(Dic dic) {
        return Mono.defer(() -> {
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
            return update(dic).then();
        });
    }

    @Override
    @WebFluxTransactional
    public Mono<Void> deleteDicByIds(String ids) {
        return Mono.defer(() -> {
            SimpleUtil.assertNotEmpty(ids, "ids不能为空");
            return Flux.fromArray(ids.split(","))
                    .map(Long::valueOf)
                    .map(id -> mapper.deleteByPrimaryKey(id) > 0)
                    .reduce((t1, t2) -> t1 && t2).then();
        });
    }

    @Override
    public Flux<Dic> queryDicByName(String name) {
        return Flux.defer(() -> {
            Dic dic = new Dic();
            dic.setName(name);
            return Flux.fromIterable(mapper.select(dic));
        });
    }
}
