package com.minsu.kim.daoujapan.security;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import com.minsu.kim.daoujapan.data.response.CommonResponse;
import com.minsu.kim.daoujapan.helper.Aes256Helper;

@RequiredArgsConstructor
@Slf4j
public class TokenFilter extends OncePerRequestFilter {

  private static final CredentialsExpiredException UNAUTHORIZED =
      new CredentialsExpiredException("로그인이 필요합니다.");
  private static final LocalDateTime DEFAULT_LOCAL_DATE_TIME =
      LocalDateTime.of(1900, 1, 1, 0, 0, 0);

  private final ObjectMapper objectMapper;
  private final ConcurrentMap<String, LocalDateTime> fakeRedis;

  @SneakyThrows
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) {
    try {
      // Bearer 토큰확인
      var tokenValue =
          Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
              .filter(token -> token.startsWith("Bearer "))
              .map(token -> token.substring(7))
              .orElseThrow(() -> UNAUTHORIZED);

      // 만료 시간 확인
      var tokenDateTime = fakeRedis.getOrDefault(tokenValue, DEFAULT_LOCAL_DATE_TIME);
      LocalDateTime now = LocalDateTime.now();

      // 페이크 레디스에 없어 기본값과 같을 경우, 예외 발생
      if (tokenDateTime.isEqual(DEFAULT_LOCAL_DATE_TIME)) {
        throw UNAUTHORIZED;
      }

      // 토큰 만료도 같음.
      if (now.isAfter(tokenDateTime)) {
        fakeRedis.remove(tokenValue);

        throw UNAUTHORIZED;
      }

      // 토큰 복호화
      String decryptedToken = Aes256Helper.decrypt(tokenValue);

      ApplicationUser userPrincipal = objectMapper.readValue(decryptedToken, ApplicationUser.class);
      var grantedAuthorityCollections =
          userPrincipal.getAuthorities().stream().map(SimpleGrantedAuthority::new).toList();
      User principal = new User(userPrincipal.getUsername(), "", grantedAuthorityCollections);

      LocalDateTime newExpirationTime = now.plusMinutes(30);
      fakeRedis.putIfAbsent(tokenValue, newExpirationTime);

      // 인증 정보 설정
      Authentication authentication =
          new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
      SecurityContextHolder.getContext().setAuthentication(authentication);

      filterChain.doFilter(request, response);

    } catch (CredentialsExpiredException e) {
      log.debug("CredentialsExpiredException : {}", e.getMessage());
      var message =
          objectMapper.writeValueAsString(CommonResponse.responseUnauthorized(e.getMessage()));

      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write(message);
    }
  }
}
