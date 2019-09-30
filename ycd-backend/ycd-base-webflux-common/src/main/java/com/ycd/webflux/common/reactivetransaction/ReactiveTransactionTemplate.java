package com.ycd.webflux.common.reactivetransaction;


import com.ycd.common.context.UserContext;
import com.ycd.common.exception.BusinessException;
import com.ycd.webflux.common.context.ReactiveUserContext;
import com.ycd.webflux.common.context.ReactiveUserContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class ReactiveTransactionTemplate extends DefaultTransactionDefinition
        implements ReactiveTransactionOperations {
    private static final long serialVersionUID = -8696657060811452628L;
    private Logger LOG = LoggerFactory.getLogger(ReactiveTransactionTemplate.class);

    @NonNull
    private final PlatformTransactionManager transactionManager;


    private static final String TRANS_STATUS_MONO_KEY = "tsmk";

    private static final String SCHEDULER = "ts_scheduler";

    ReactiveTransactionTemplate(@NonNull PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    private Scheduler pool = Schedulers.newElastic("T_X");


    /**
     * 执行和返回mono
     *
     * @param action 动作
     * @param <T>    泛型
     * @return 结果
     */
    @Override
    @NonNull
    public <T> Mono<T> executeAndReturnMono(@NonNull MonoTransactionCallback<T> action) {
        TransactionStatusWrapper wrapper = new TransactionStatusWrapper();
        //获取事务以及运行环境
        return ReactiveUserContextHolder.userContext()
                .switchIfEmpty(Mono.error(new BusinessException("无用户信息")))
                .flatMap(this::transactionalStatusAndRunEnvironment)
                .flatMap(status -> {
                    wrapper.setStatus(status);
                    //设置事务状态
                    return doAndError(action, wrapper);
                }).doOnSuccess(val -> commitTransaction(wrapper))
                .doFinally(t -> clearTransactionStatus(wrapper))
                .subscriberContext(context -> initContext(context, wrapper)).publishOn(Schedulers.elastic());
    }

    private Mono<TransactionStatus> transactionalStatusAndRunEnvironment(ReactiveUserContext reactiveUserContext) {
        return Mono.subscriberContext().flatMap(context -> {
            Mono<TransactionStatus> transStatusMono = context.get(TRANS_STATUS_MONO_KEY);
            return Mono.just(true).publishOn(context.get(SCHEDULER))
                    .flatMap(b -> {
                        UserContext.setUser(reactiveUserContext.getUser());
                        return transStatusMono;
                    });
        });
    }

    private void clearTransactionStatus(TransactionStatusWrapper wrapper) {
        UserContext.remove();
        wrapper.clear();
    }

    private void commitTransaction(TransactionStatusWrapper wrapper) {
        transactionManager.commit(wrapper.getStatus());
        LOG.debug("{}Mono事务{}被正常提交", LocalTime.now(), wrapper.getStatus());
    }

    private Context initContext(Context context, TransactionStatusWrapper wrapper) {
        if (context.hasKey(SCHEDULER)) {
            Mono<TransactionStatus> statusMono = context.get(TRANS_STATUS_MONO_KEY);
            //得到运行环境
            Scheduler scheduler = context.get(SCHEDULER);
            wrapper.setStatusMono(statusMono);
            wrapper.setNewTransaction(false);
            wrapper.setRunScheduler(scheduler);
            return context;
        }
        ReactiveTransactionTemplate transactionDef = this;
        //开启新的事务
        Mono<TransactionStatus> statusMono = Mono.defer(() -> {
                    TransactionStatus status = transactionManager.getTransaction(transactionDef);
                    if (status.isNewTransaction()) {
                        LOG.debug("{}Mono事务{}被正常开启", LocalTime.now(), status);
                    } else {
                        LOG.debug("{}Mono事务{}已经存在，加入这个事务运行", LocalTime.now(), status);
                    }
                    return Mono.just(status);
                }
        );
        Scheduler scheduler = Schedulers.single(pool);
        wrapper.setStatusMono(statusMono);
        wrapper.setRunScheduler(scheduler);
        return context.putAll(Context.of(SCHEDULER, scheduler))
                .putAll(Context.of(TRANS_STATUS_MONO_KEY, statusMono));
    }

    /**
     * 执行和返回flux
     *
     * @param action 动作
     * @param <T>    泛型
     * @return flux
     */
    @Override
    public <T> Flux<T> executeAndReturnFlux(FluxTransactionCallback<T> action) {
        TransactionStatusWrapper wrapper = new TransactionStatusWrapper();
        return ReactiveUserContextHolder.userContext()
                .switchIfEmpty(Mono.error(new BusinessException("无用户信息")))
                .flatMap(this::transactionalStatusAndRunEnvironment)
                .flux().flatMap(status -> {
                    wrapper.setStatus(status);
                    return doAndErrorReturnFlux(action, wrapper);
                }).doOnComplete(() -> {
                    commitTransaction(wrapper);
                }).doFinally(t -> clearTransactionStatus(wrapper))
                .subscriberContext(context -> initContext(context, wrapper)).publishOn(Schedulers.elastic());
    }

    private <T> Mono<T> doAndError(@NonNull MonoTransactionCallback<T> action,
                                   @NonNull TransactionStatusWrapper wrapper) {
        try {
            return action.doInTransactionAndReturnMono(wrapper.getStatus())
                    .publishOn(wrapper.getRunScheduler())
                    .doOnError(e -> {
                        rollbackTransaction(wrapper, e);
                    });
        } catch (Exception e) {
            rollbackTransaction(wrapper, e);
            return null;
        }
    }

    public void rollbackTransaction(TransactionStatusWrapper wrapper, Throwable e) {
        if (e instanceof BusinessException) {
            LOG.debug("{}业务异常产生,Mono事务{}进行回滚", LocalTime.now(), wrapper.getStatus());
        } else {
            LOG.error("{}非业务异常产生Mono事务{}进行回滚", LocalTime.now(), wrapper.getStatus(), e);
        }
        wrapper.getStatus().setRollbackOnly();
        transactionManager.rollback(wrapper.getStatus());
        //将异常传递给上一个调用者
        if (e instanceof BusinessException) {
            throw (BusinessException) e;
        }
        throw new RuntimeException(e);
    }

    private <T> Flux<T> doAndErrorReturnFlux(@NonNull FluxTransactionCallback<T> action,
                                             @NonNull TransactionStatusWrapper wrapper) {
        try {
            return action.doInTransactionAndReturnFlux(wrapper.getStatus())
                    .publishOn(wrapper.getRunScheduler())
                    .doOnError(e -> {
                        rollbackTransaction(wrapper, e);
                    });
        } catch (Exception e) {
            rollbackTransaction(wrapper, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {


        Scheduler scheduler = Schedulers.single();
        scheduler.schedule(() -> {
            System.out.println("nihao");
        });
        TimeUnit.SECONDS.sleep(1);
    }

    public Object doBlockedProcess(ProceedingJoinPoint point) {
        TransactionStatus status = transactionManager.getTransaction(this);
        try {
            return point.proceed();
        } catch (Throwable e) {
            LOG.debug("{}异常产生.阻塞事务{}进行回滚", LocalTime.now(), status);
            if (status.isNewTransaction())
                transactionManager.rollback(status);
        }
        return null;
    }


    private static class TransactionStatusWrapper {
        private Mono<TransactionStatus> statusMono;

        private TransactionStatus status;


        private Scheduler runScheduler;

        private boolean newTransaction = true;


        public Scheduler getRunScheduler() {
            return runScheduler;
        }

        public void setRunScheduler(Scheduler runScheduler) {
            this.runScheduler = runScheduler;
        }

        public Mono<TransactionStatus> getStatusMono() {
            return statusMono;
        }

        public void setStatusMono(Mono<TransactionStatus> statusMono) {
            this.statusMono = statusMono;
        }

        public TransactionStatus getStatus() {
            return status;
        }

        public void setStatus(TransactionStatus status) {
            this.status = status;
        }


        public boolean isNewTransaction() {
            return newTransaction;
        }

        public void setNewTransaction(boolean newTansaction) {
            this.newTransaction = newTansaction;
        }

        public void clear() {
            if (newTransaction) {
                runScheduler.dispose();
            }
            status = null;
            statusMono = null;
            newTransaction = true;

        }
    }

}
