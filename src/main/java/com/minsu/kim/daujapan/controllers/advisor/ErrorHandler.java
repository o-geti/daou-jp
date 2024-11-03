package com.minsu.kim.daujapan.controllers.advisor;

import com.minsu.kim.daujapan.exception.ValidateCheckError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.minsu.kim.daujapan.data.response.CommonResponse;
import com.minsu.kim.daujapan.exception.NotFoundException;
import com.minsu.kim.daujapan.helper.StackTraceUtil;

/**
 * 공통적으로 발생되는 예외 혹은 에러를 잡아 핸들링합니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@RestControllerAdvice
@Slf4j
public class ErrorHandler {
  @ExceptionHandler({
    MethodArgumentNotValidException.class,
    MissingServletRequestParameterException.class,
    ValidateCheckError.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public CommonResponse<String> exceptValidation400Error(Exception exception) {
    log.info(StackTraceUtil.filterStackTracePackage(exception));

    return CommonResponse.responseBadRequest(exception.getMessage());
  }

  @ExceptionHandler({NotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public CommonResponse<String> exceptValidation404Error(NotFoundException exception) {
    return CommonResponse.responseNotFound(exception.getMessage());
  }

  @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public CommonResponse<String> exceptValidation404Error(
      HttpRequestMethodNotSupportedException exception) {
    return CommonResponse.responseNotAllow(exception.getMessage());
  }

  @ExceptionHandler({Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CommonResponse<String> except500Error(Throwable exception) {
    log.error(StackTraceUtil.printStackTrace(exception));

    return CommonResponse.responseServerError(exception.getMessage());
  }
}
