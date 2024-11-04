package com.minsu.kim.daoujapan.domains.statistics;

import java.time.LocalDateTime;

import jakarta.persistence.MappedSuperclass;

import lombok.Getter;

/**
 * @author minsu.kim
 * @since 1.0
 */
@MappedSuperclass
@Getter
public class BaseEntity {

  private LocalDateTime deleteDt;

  protected void setDeleteDt(LocalDateTime deleteDt) {
    this.deleteDt = deleteDt;
  }
}
