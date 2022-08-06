package com.f3f.community.common.configuration.Intercepter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Component
@RequiredArgsConstructor
public class LoginCheckIntercepter implements HandlerInterceptor {

    //private final SessionLoginService sessionLoginService;

    private Environment environment;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }
}

