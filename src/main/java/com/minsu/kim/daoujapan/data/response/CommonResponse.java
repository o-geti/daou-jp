package com.minsu.kim.daoujapan.data.response;

import org.springframework.http.HttpStatus;

/**
 * 공통 처리 응답 객체입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
public record CommonResponse<T>(int status, T data) {

  public static <T> CommonResponse<T> responseSuccess(T data) {
    return new CommonResponse<>(HttpStatus.OK.value(), data);
  }

  public static <T> CommonResponse<T> responseCreated(T data) {
    return new CommonResponse<>(HttpStatus.CREATED.value(), data);
  }

  public static <T> CommonResponse<T> responseBadRequest(T data) {
    return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(), data);
  }

  public static <T> CommonResponse<T> responseNotFound(T data) {
    return new CommonResponse<>(HttpStatus.NOT_FOUND.value(), data);
  }

  public static <T> CommonResponse<T> responseNotAllow(T data) {
    return new CommonResponse<>(HttpStatus.METHOD_NOT_ALLOWED.value(), data);
  }

  public static <T> CommonResponse<T> responseConflict(T data) {
    return new CommonResponse<>(HttpStatus.CONFLICT.value(), data);
  }

  public static <T> CommonResponse<T> responseServerError(T data) {
    return new CommonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), data);
  }
}
