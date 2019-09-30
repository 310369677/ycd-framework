package com.ycd.servlet.common.filter;


import com.ycd.common.context.PageContext;
import com.ycd.common.dto.Page;
import com.ycd.common.util.SimpleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 分页过滤器
 */
public class PageFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(PageFilter.class);


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        Page page = new Page<>();
        page.setPageNum(1);
        page.setPageSize(20);
        String pageSize = request.getParameter("pageSize");
        String pageNum = request.getParameter("pageNum");
        if (SimpleUtil.isNotEmpty(pageSize) && SimpleUtil.isNotEmpty(pageNum)) {
            page.setPageNum(Integer.valueOf(pageNum));
            page.setPageSize(Integer.valueOf(pageSize));
        }
        PageContext.setPage(page);
        HttpServletRequest httpServletRequest= (HttpServletRequest) request;
        log.debug("请求{} 分页参数[pageSize:{},pageNum:{}]",httpServletRequest.getRequestURI(),page.getPageSize(),page.getPageNum());
        chain.doFilter(request, response);
    }
}
