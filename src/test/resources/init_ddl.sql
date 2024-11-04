DROP TABLE IF EXISTS subscriber_statistics;
CREATE TABLE IF NOT EXISTS subscriber_statistics
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_time      TIMESTAMP NOT NULL,
    subscriber_count INT       NOT NULL,
    UNIQUE INDEX idx_subscriber_record_time (record_time)
);

DROP TABLE IF EXISTS leaver_statistics;
CREATE TABLE IF NOT EXISTS leaver_statistics
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_time  TIMESTAMP NOT NULL,
    leaver_count INT       NOT NULL,
    UNIQUE INDEX idx_leaver_record_time (record_time)
);

DROP TABLE IF EXISTS payment_amount_statistics;
CREATE TABLE IF NOT EXISTS payment_amount_statistics
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_time    TIMESTAMP NOT NULL,
    payment_amount BIGINT    NOT NULL,
    UNIQUE INDEX idx_payment_record_time (record_time)
);

DROP TABLE IF EXISTS usage_amount_statistics;
CREATE TABLE IF NOT EXISTS usage_amount_statistics
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_time  TIMESTAMP NOT NULL,
    usage_amount BIGINT    NOT NULL,
    UNIQUE INDEX idx_usage_record_time (record_time)
);

DROP TABLE IF EXISTS sales_amount_statistics;
CREATE TABLE IF NOT EXISTS sales_amount_statistics
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_time  TIMESTAMP NOT NULL,
    sales_amount BIGINT    NOT NULL,
    UNIQUE INDEX idx_sales_record_time (record_time)
);