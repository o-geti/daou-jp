package com.minsu.kim.daoujapan.controllers.advisor;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.minsu.kim.daoujapan.data.response.CommonResponse;
import com.minsu.kim.daoujapan.exception.NotFoundException;
import com.minsu.kim.daoujapan.exception.StatisticIsAlreadyExist;
import com.minsu.kim.daoujapan.exception.ValidateCheckError;
import com.minsu.kim.daoujapan.helper.StackTraceUtil;

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
    MissingServletRequestParameterException.class,
    HttpMessageNotReadableException.class,
    ValidateCheckError.class,
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public CommonResponse<String> exceptValidation400Error(Exception exception) {
    log.info(StackTraceUtil.filterStackTracePackage(exception));

    return CommonResponse.responseBadRequest(exception.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public CommonResponse<List<String>> exceptValidation400Error(
      MethodArgumentNotValidException exception) {

    return CommonResponse.responseBadRequest(
        exception.getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .toList());
  }

  @ExceptionHandler({NotFoundException.class, NoResourceFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public CommonResponse<String> exceptValidation404Error(Exception exception) {
    return CommonResponse.responseNotFound(exception.getMessage());
  }

  @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public CommonResponse<String> exceptValidation404Error(
      HttpRequestMethodNotSupportedException exception) {
    return CommonResponse.responseNotAllow(exception.getMessage());
  }

  @ExceptionHandler({StatisticIsAlreadyExist.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  public CommonResponse<String> exceptValidation409Error(Exception exception) {
    return CommonResponse.responseConflict(exception.getMessage());
  }

  @ExceptionHandler({Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CommonResponse<String> except500Error(Throwable exception) {
    log.error(StackTraceUtil.printStackTrace(exception));

    return CommonResponse.responseServerError(exception.getMessage());
  }
}
