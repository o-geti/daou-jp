package com.minsu.kim.daoujapan.config;

import com.minsu.kim.daoujapan.security.AccessWhiteIpAddressFilter;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.minsu.kim.daoujapan.security.RequestProcessTimeMarkerFilter;
import com.minsu.kim.daoujapan.security.TokenFilter;
import com.minsu.kim.daoujapan.security.AccessDinedHandlerImpl;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {
  private static final RequestProcessTimeMarkerFilter API_TIME_MARKER_FILTER =
      new RequestProcessTimeMarkerFilter();

  private final ObjectMapper objectMapper;
  private final WhiteListIpConfig whiteListIpConfig;

  @Bean
  public ConcurrentMap<String, LocalDateTime> getFakeRedis() {
    return new ConcurrentHashMap<>();
  }

  @Order(2)
  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, AccessDinedHandlerImpl accessDinedHandler) throws Exception {
    http.securityMatcher("/**")
        .authorizeHttpRequests(
            (authorize) -> {
              authorize
                  .requestMatchers("/v1/**")
                  .authenticated()
                  .requestMatchers("/", "/error/**")
                  .anonymous()
                  .anyRequest()
                  .denyAll();
            })
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .sessionManagement(
            sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(
            new TokenFilter(objectMapper, getFakeRedis()),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(API_TIME_MARKER_FILTER, LogoutFilter.class)
        .addFilterAfter(new AccessWhiteIpAddressFilter(whiteListIpConfig, objectMapper), RequestProcessTimeMarkerFilter.class)
        .exceptionHandling(exceptConfig -> exceptConfig.accessDeniedHandler(accessDinedHandler));

    return http.build();
  }

  @Order(1)
  @Bean
  public SecurityFilterChain securityUsernamePasswordFilterChain(
      HttpSecurity http,
      AuthenticationSuccessHandler successHandler,
      AccessDinedHandlerImpl accessDinedHandler)
      throws Exception {

    http.securityMatcher("/login")
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .sessionManagement(
            sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .formLogin(
            formLoginConfigurer -> {
              formLoginConfigurer.usernameParameter("username");
              formLoginConfigurer.passwordParameter("password");
              formLoginConfigurer.successHandler(successHandler);
            })
        .logout(LogoutConfigurer::disable)
        .addFilterBefore(API_TIME_MARKER_FILTER, LogoutFilter.class)
        .addFilterAfter(new AccessWhiteIpAddressFilter(whiteListIpConfig, objectMapper), RequestProcessTimeMarkerFilter.class)
        .exceptionHandling(exceptConfig -> exceptConfig.accessDeniedHandler(accessDinedHandler));

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService(DataSource dataSource) {
    var jdbcUserDetailService = new JdbcUserDetailsManagerConfigurer<>();

    return jdbcUserDetailService
        .dataSource(dataSource)
        .passwordEncoder(passwordEncoder())
        .usersByUsernameQuery(
            "SELECT username, password, true as 'enable' FROM users " + "where username = ?")
        .authoritiesByUsernameQuery(
            "SELECT username, 'ROLE_USER' as authority FROM users where username = ?")
        .getUserDetailsService();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring().requestMatchers("/swagger-ui/**", "/api-docs/**");
  }
}
