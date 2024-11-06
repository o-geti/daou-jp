package com.minsu.kim.daoujapan.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.minsu.kim.daoujapan.data.response.CommonResponse;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AccessDinedHandlerImpl implements AccessDeniedHandler {
  private final ObjectMapper objectMapper;

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {

    var errorMessage =
        objectMapper.writeValueAsString(CommonResponse.responseForbidden("접근 권한이 없습니다."));

    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType("application/json;charset=UTF-8");
    response.sendError(HttpStatus.FORBIDDEN.value(), errorMessage);
  }
}
