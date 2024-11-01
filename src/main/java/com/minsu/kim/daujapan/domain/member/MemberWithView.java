package com.minsu.kim.daujapan.domain.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Data
@Schema(description = "크레딧 캠페인 정보")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberWithView {

  private Long signUpMemberCount;

  /**
   * 자세한 기능을 적어주세요
   *
   * @author minsu.kim
   * @since 1.0
   */
  @Data
  @ToString
  public static class Filter {
    private int a;
    private int b;
    //      private LocalDateTime startDateTime;
    //      private LocalDateTime endDateTime = LocalDateTime.now();
  }
}
