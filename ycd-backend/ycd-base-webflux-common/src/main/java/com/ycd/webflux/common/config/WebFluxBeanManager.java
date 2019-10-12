package com.ycd.webflux.common.config;

import com.ycd.common.dto.Page;
import com.ycd.common.exception.BusinessException;
import com.ycd.common.util.SimpleUtil;
import com.ycd.webflux.common.cache.InnerReactiveUserMultiLoginCache;
import com.ycd.webflux.common.cache.InnerReactiveUserSingleLoginCache;
import com.ycd.webflux.common.cache.ReactiveRedisUserCache;
import com.ycd.webflux.common.cache.UserCacheProxyReactive;
import com.ycd.webflux.common.context.ReactivePageHolder;
import com.ycd.webflux.common.exceptionHandler.GlobalErrorAttributes;
import com.ycd.webflux.common.exceptionHandler.GlobleErrorWebException;
import com.ycd.webflux.common.reactivetransaction.ReactiveTransaction;
import com.ycd.webflux.common.service.impl.ReactiveDicServiceImpl;
import com.ycd.webflux.common.service.impl.ReactiveDocumentServiceImpl;
import com.ycd.webflux.common.service.interfaces.ReactiveDicService;
import com.ycd.webflux.common.service.interfaces.ReactiveDocumentService;
import com.ycd.webflux.common.web.ReactiveDicController;
import com.ycd.webflux.common.web.ReactiveDocumentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class WebFluxBeanManager {

    private static final Logger log = LoggerFactory.getLogger(PlatformWebFluxConfigurationSupport.class);

    @Bean
    public ReactiveDocumentController documentController() {
        return new ReactiveDocumentController();
    }

    @Bean
    public ReactiveDocumentService reactiveDocumentService() {
        return new ReactiveDocumentServiceImpl();
    }


    @Bean
    public ReactiveDicController dicController() {
        return new ReactiveDicController();
    }

    @Bean
    public ReactiveDicService dicService() {
        return new ReactiveDicServiceImpl();
    }

    @Bean
    public ReactiveRedisUserCache userCacheProxy() {
        return new UserCacheProxyReactive();
    }

    @Bean
    public InnerReactiveUserSingleLoginCache userSingleLoginCache() {
        return new InnerReactiveUserSingleLoginCache();
    }

    @Bean
    public InnerReactiveUserMultiLoginCache userMultiLoginCache() {
        return new InnerReactiveUserMultiLoginCache();
    }

    @Bean
    @ConditionalOnClass(PlatformTransactionManager.class)
    public ReactiveTransaction reactiveTransaction(PlatformTransactionManager platformTransactionManager) {
        return new ReactiveTransaction(platformTransactionManager);
    }


    /**
     * 上下文过略器
     *
     * @param serverProperties serverProperties
     * @return 过虑器
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebFilter contextPathWebFilter(ServerProperties serverProperties) {
        String contextPath = serverProperties.getServlet().getContextPath();
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (SimpleUtil.isNotEmpty(contextPath) && !request.getURI().getPath().startsWith(contextPath)) {
                throw new BusinessException(String.format("无效的请求路径:%s", request.getURI().getPath()));
            }

            return chain.filter(exchange.mutate()
                    .request(request.mutate().contextPath(contextPath).build())
                    .build());
        };
    }


    /**
     * 分页过略器
     *
     * @return WebFilter
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 100)
    public WebFilter pageWebFilter() {
        return (exchange, chain) -> {
            Page page = new Page<>();
            page.setPageNum(1);
            page.setPageSize(20);
            Mono<Page> m = Mono.just(page);
            //获取请求参数
            MultiValueMap map = exchange.getRequest().getQueryParams();
            Mono<MultiValueMap<String, String>> formData = exchange.getFormData();
            String pageSize = (String) map.get("pageSize");
            String pageNum = (String) map.get("pageNum");
            log.debug("请求{}的分页参数[pageSize:{},pageNum:{}]", exchange.getRequest().getURI(),SimpleUtil.safeVal(pageSize,page.getPageSize()), SimpleUtil.safeVal(pageNum,page.getPageNum()));
            if (SimpleUtil.isNotEmpty(pageSize) && SimpleUtil.isNotEmpty(pageNum)) {
                m.map(page1 -> {
                    page1.setPageNum(Integer.valueOf(pageNum));
                    page1.setPageSize(Integer.valueOf(pageSize));
                    return page1;
                });
            }
            m = m.flatMap(page2 -> formData.map(map1 -> {
                List<String> pageSize1 = map1.get("pageSize");
                List<String> pageNum1 = map1.get("pageNum");
                if (SimpleUtil.isNotEmpty(pageSize1) && SimpleUtil.isNotEmpty(pageNum1)) {
                    page2.setPageSize(Integer.valueOf(pageSize1.get(0)));
                    page2.setPageNum(Integer.valueOf(pageNum1.get(0)));
                }
                return page2;
            }));
            return chain.filter(exchange).subscriberContext(ReactivePageHolder.withPageMono(m));
        };
    }


    @Bean
    @Order(-2)
    public GlobleErrorWebException globleErrorWebException(ResourceProperties resourceProperties, ApplicationContext applicationContext) {
        return new GlobleErrorWebException(new GlobalErrorAttributes(), resourceProperties, applicationContext);
    }
}
