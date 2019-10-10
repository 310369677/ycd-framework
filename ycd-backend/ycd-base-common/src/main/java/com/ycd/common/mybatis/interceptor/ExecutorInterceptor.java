package com.ycd.common.mybatis.interceptor;


import com.ycd.common.context.UserContext;
import com.ycd.common.entity.AbstractEntity;
import com.ycd.common.util.IDUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import java.util.Properties;


//拦截器
@Intercepts({
        @Signature(type = Executor.class,
                method = "update",
                args = {MappedStatement.class,
                        Object.class}),
})
public class ExecutorInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object param = invocation.getArgs()[1];
        if (param instanceof AbstractEntity) {
            AbstractEntity entity = (AbstractEntity) param;
            if (SqlCommandType.INSERT == sqlCommandType) {
                entity.setId(IDUtil.id());
                String currentTime = System.currentTimeMillis() + "";
                entity.setCreateTime(currentTime);
                entity.setUpdateTime(currentTime);
                //TODO 当前的上下文中获取用户id
                Long id = UserContext.getCurrentUser().getId();
                entity.setCreateUserId(id + "");
                entity.setUpdateUserId(id + "");
                entity.setVersion(0L);
            } else if (SqlCommandType.UPDATE == sqlCommandType) {
                entity.setUpdateTime(System.currentTimeMillis() + "");
                entity.setCreateTime(null);
                entity.setUpdateUserId(UserContext.getCurrentUser().getId() + "");
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
