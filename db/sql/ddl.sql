CREATE TABLE IF NOT EXISTS `subscriber_statistics`
(
    `id`               BIGINT   NOT NULL AUTO_INCREMENT,
    `record_time`      DATETIME NOT NULL,
    `subscriber_count` INT      NOT NULL,
    `delete_dt`        DATETIME NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_subscriber_record_time` (`record_time`)
);

CREATE TABLE IF NOT EXISTS `leaver_statistics`
(
    `id`           BIGINT   NOT NULL AUTO_INCREMENT,
    `record_time`  DATETIME NOT NULL,
    `leaver_count` INT      NOT NULL,
    `delete_dt`    DATETIME NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_leaver_record_time` (`record_time`)
);

CREATE TABLE IF NOT EXISTS `payment_amount_statistics`
(
    `id`             BIGINT   NOT NULL AUTO_INCREMENT,
    `record_time`    DATETIME NOT NULL,
    `payment_amount` BIGINT   NOT NULL,
    `delete_dt`      DATETIME NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_payment_record_time` (`record_time`)
);

CREATE TABLE IF NOT EXISTS `usage_amount_statistics`
(
    `id`           BIGINT   NOT NULL AUTO_INCREMENT,
    `record_time`  DATETIME NOT NULL,
    `usage_amount` BIGINT   NOT NULL,
    `delete_dt`    DATETIME NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_usage_record_time` (`record_time`)
);

CREATE TABLE IF NOT EXISTS `sales_amount_statistics`
(
    `id`           BIGINT   NOT NULL AUTO_INCREMENT,
    `record_time`  DATETIME NOT NULL,
    `sales_amount` BIGINT   NOT NULL,
    `delete_dt`    DATETIME NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_sales_record_time` (`record_time`)
);

CREATE TABLE IF NOT EXISTS `users`
(
    `id`       BIGINT       NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(100) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_username` (`username`)
);

