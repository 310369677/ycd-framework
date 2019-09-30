package com.ycd.servlet.security.security;


import com.ycd.common.config.CommonConfig;
import com.ycd.common.context.UserContext;
import com.ycd.common.entity.AbstractEntity;
import com.ycd.common.entity.User;
import com.ycd.common.util.SimpleUtil;
import com.ycd.servlet.security.entity.SecurityUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenSecurityContextRepository implements SecurityContextRepository {

    private static final Logger logger = LoggerFactory.getLogger(TokenSecurityContextRepository.class);

    @Autowired
    private CommonConfig config;

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();

        SecurityContext context = readSecurityContextFromRequest(request);

        if (context == null) {
            context = generateNewContext();
            logger.trace("没有从request找到上下文，将会产生空的上下文{}", context);
        }
        return context;
    }

    private SecurityContext generateNewContext() {
        return SecurityContextHolder.createEmptyContext();
    }

    private SecurityContext readSecurityContextFromRequest(HttpServletRequest request) {
        String token = request.getHeader(config.getTokenAuthHead());
        if (SimpleUtil.isEmpty(token)) {
            return null;
        }
        User user = UserContext.getCurrentUser();
        if (SimpleUtil.isEmpty(user)) {
            return null;
        }
        SecurityContext context = SecurityContextHolder.getContext();
        SecurityUserDetails userDetails = new SecurityUserDetails(user);
        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(userDetails.getUsername(), "", userDetails.getAuthorities());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principal, null, userDetails.getAuthorities());
        context.setAuthentication(usernamePasswordAuthenticationToken);
        return context;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return SimpleUtil.isNotEmpty(UserContext.getCurrentUser());
    }
}
