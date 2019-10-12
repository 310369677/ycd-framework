package com.ycd.servlet.common.exception.handle;


import com.ycd.common.Result;
import com.ycd.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GlobalExceptionHandler implements HandlerExceptionResolver {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
        if (ex instanceof BusinessException) {
            logger.debug("业务异常", ex);
            mv.addObject(Result.error(ex.getMessage()));
            return mv;
        }
        logger.error("非业务异常", ex);
        mv.addObject(Result.error("服务器内部异常"));
        return mv;
    }
}
