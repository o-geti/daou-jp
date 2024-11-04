package com.minsu.kim.daoujapan.data.response;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 공통 페이지 정보객체입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
public record Paging<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    int totalPageCount,
    boolean hasNext,
    boolean hasPrevious) {

  public static <T, R> Paging<R> createPaging(
      Page<T> pageResult, Pageable pageable, Function<T, R> contentConverter) {
    var currentPage = pageable.getPageNumber();
    var totalPage = pageResult.getTotalPages();
    var content = pageResult.getContent().stream().map(contentConverter).toList();

    return new Paging<>(
        content,
        currentPage,
        pageable.getPageSize(),
        totalPage,
        currentPage != (totalPage - 1),
        currentPage != 0);
  }
}
