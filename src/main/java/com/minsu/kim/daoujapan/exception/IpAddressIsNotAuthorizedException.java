package com.minsu.kim.daoujapan.exception;

/**
 * @author minsu.kim
 * @since 1.0
 */
public class IpAddressIsNotAuthorizedException extends RuntimeException {

  public IpAddressIsNotAuthorizedException() {
    super("허기되지않은 IP입니다.");
  }
}
