package com.minsu.kim.daoujapan.security;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.minsu.kim.daoujapan.data.response.CommonResponse;
import com.minsu.kim.daoujapan.helper.Aes256Helper;

/**
 * >
 *
 * @author minsu.kim
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
  private final ObjectMapper objectMapper;
  private final ConcurrentMap<String, LocalDateTime> fakeRedis;
  private static final Long TOKEN_EXPIRE_TIME = 30L;

  @SneakyThrows
  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    var user = (User) authentication.getPrincipal();

    var applicationUser =
        new ApplicationUser(
            LocalDateTime.now().plusMinutes(TOKEN_EXPIRE_TIME),
            user.getUsername(),
            user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

    var principalString = objectMapper.writeValueAsString(applicationUser);

    log.info(principalString);
    var token = Aes256Helper.encrypt(principalString);

    fakeRedis.put(token, applicationUser.getTokenExpireTime());

    response.setStatus(HttpStatus.OK.value());
    response.setContentType("application/json;charset=UTF-8");
    response
        .getWriter()
        .write(objectMapper.writeValueAsString(CommonResponse.responseSuccess(token)));
  }
}
