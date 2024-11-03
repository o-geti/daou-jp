package com.minsu.kim.daujapan.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author minsu.kim
 * @since 1.0
 */
@Component
@Slf4j
public class RequestProcessTimeMarkerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        log.info("-------------요청 처리 시작 -------------");
        log.info("path = {}", request.getRequestURI());
        log.info("query = {}", request.getQueryString());
        log.info("method = {}", request.getMethod());
        log.info("time: {}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss;SSS")));
        log.info("--------------------------------------");
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        log.info("-------------요청 처리 종료 -------------");
        log.info("path = {}", request.getRequestURI());
        log.info("query = {}", request.getQueryString());
        log.info("method = {}", request.getMethod());
        log.info("time: {}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss;SSS")));
        log.info("--------------------------------------");
    }
}
