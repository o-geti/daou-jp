package com.minsu.kim.daoujapan.security;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUser {

  private LocalDateTime tokenExpireTime;
  private String username;
  private List<String> authorities;
}
