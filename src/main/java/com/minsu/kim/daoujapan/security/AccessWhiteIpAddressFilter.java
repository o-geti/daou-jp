package com.minsu.kim.daoujapan.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.minsu.kim.daoujapan.config.WhiteListIpConfig;
import com.minsu.kim.daoujapan.data.response.CommonResponse;
import com.minsu.kim.daoujapan.exception.IpAddressIsNotAuthorizedException;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
public class AccessWhiteIpAddressFilter extends OncePerRequestFilter {
  private final WhiteListIpConfig whiteListIpConfig;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    var hasRemoteAddressInWhiteListIp =
        whiteListIpConfig.getIpList().stream()
            .anyMatch(ipString -> request.getRemoteAddr().equals(ipString));

    if (!hasRemoteAddressInWhiteListIp) {
      log.warn("인가되지않은 IP 접근요청 발생 IP = {}", request.getRemoteAddr());
      log.warn("인가되지않은 IP 접근요청 발생 HOST = {}", request.getRemoteHost());

      var responseMessage =
          CommonResponse.responseUnauthorized(new IpAddressIsNotAuthorizedException().getMessage());

      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType("application/json;charset=UTF-8");
      response
          .getWriter()
          .write(objectMapper.writeValueAsString(CommonResponse.responseSuccess(responseMessage)));
      return;
    }

    log.debug("IP 확인 : {}", request.getRemoteAddr());
    filterChain.doFilter(request, response);
  }
}
