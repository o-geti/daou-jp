CREATE TABLE `subscriber_statistics`
(
    `id`               BIGINT   NOT NULL AUTO_INCREMENT,
    `record_time`      DATETIME NOT NULL,
    `subscriber_count` INT      NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_subscriber_record_time` (`record_time`)
);

CREATE TABLE `leaver_statistics`
(
    `id`           BIGINT   NOT NULL AUTO_INCREMENT,
    `record_time`  DATETIME NOT NULL,
    `leaver_count` INT      NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_leaver_record_time` (`record_time`)
);

CREATE TABLE `payment_amount_statistics`
(
    `id`             BIGINT   NOT NULL AUTO_INCREMENT,
    `record_time`    DATETIME NOT NULL,
    `payment_amount` BIGINT   NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_payment_record_time` (`record_time`)
);

CREATE TABLE `usage_amount_statistics`
(
    `id`           BIGINT   NOT NULL AUTO_INCREMENT,
    `record_time`  DATETIME NOT NULL,
    `usage_amount` BIGINT   NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_usage_record_time` (`record_time`)
);

CREATE TABLE `sales_amount_statistics`
(
    `id`           BIGINT   NOT NULL AUTO_INCREMENT,
    `record_time`  DATETIME NOT NULL,
    `sales_amount` BIGINT   NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_sales_record_time` (`record_time`)
);
