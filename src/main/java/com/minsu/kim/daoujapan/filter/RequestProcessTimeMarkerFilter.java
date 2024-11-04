package com.minsu.kim.daoujapan.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Slf4j
public class RequestProcessTimeMarkerFilter implements Filter {
  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws ServletException, IOException {

    var requestId = UUID.randomUUID();

    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

    httpServletRequest.setAttribute("requestId", requestId);

    printRequestPrinter(httpServletRequest);

    filterChain.doFilter(servletRequest, servletResponse);

    printRequestPrinter(httpServletRequest);
  }

  private void printRequestPrinter(HttpServletRequest servletRequest) {

    log.info("-------------요청 처리 ID : {} -------------", servletRequest.getAttribute("requestId"));
    log.info("path = {}", servletRequest.getPathInfo());
    log.info("query = {}", servletRequest.getQueryString());
    log.info("method = {}", servletRequest.getMethod());
    log.info(
        "time: {}",
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss;SSS")));
    log.info("----------------------------------------------------------------------------");
  }
}
