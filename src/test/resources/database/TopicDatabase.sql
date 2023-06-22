DROP TABLE IF EXISTS topic_group_x_topic;
DROP TABLE IF EXISTS `topic`;
DROP TABLE IF EXISTS `topic_group`;
DROP SEQUENCE IF EXISTS sequence_topic_group_x_topic;
DROP SEQUENCE IF EXISTS sequence_topic_group;

CREATE TABLE IF NOT EXISTS `topic`
(
    `id`                     bigint(21)   NOT NULL,
    `tenant_id`              bigint(21)   NULL,
    `name`                   varchar(100) NOT NULL,
    `description`            varchar(100) NULL,
    `status`                 varchar(20),
    `create_date`            datetime     NOT NULL,
    `update_date`            datetime     NULL,
    `internal_identifier`    varchar(50)  NULL,
    `fallback_agency_id`     bigint(21)   NULL,
    `fallback_url`           varchar(200) NULL,
    `welcome_message`        varchar(200) NULL,
    `send_next_step_message` boolean DEFAULT FALSE,
    `titles_default`         varchar(255) NOT NULL,
    `titles_short`           varchar(255) NOT NULL,
    `titles_long`            varchar(255) NOT NULL,
    `titles_welcome`         varchar(255) NOT NULL,
    `titles_dropdown`        varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
);

ALTER TABLE `topic`
    ALTER COLUMN `create_date` SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE `topic`
    ALTER COLUMN `update_date` SET DEFAULT CURRENT_TIMESTAMP;

CREATE SEQUENCE IF NOT EXISTS sequence_topic
    INCREMENT BY 1
    START WITH 100000;

INSERT INTO TOPIC (`id`, `tenant_id`, `name`, `description`, `status`, `create_date`, `fallback_agency_id`,
                   `fallback_url`, `welcome_message`, `send_next_step_message`, `titles_default`, `titles_short`,
                   `titles_long`, `titles_welcome`, `titles_dropdown`)
VALUES (1, '1', '{"de" : "de an active topic", "en": "en an active topic"}',
        '{"de" : "de description", "en": "en description"}', 'ACTIVE',
        '2022-06-02', 1, 'https://www.google.com', 'Welcome', TRUE, '1-default', '1-short',
        '1-long', '1-welcome', '1-dropdown');

INSERT INTO TOPIC (`id`, `tenant_id`, `name`, `description`, `status`, `create_date`, `titles_default`, `titles_short`,
                   `titles_long`, `titles_welcome`, `titles_dropdown`)
VALUES (2, '1', '{"de" : "de not an active topic", "en": "en not an active topic"}',
        '{"de" : "de description", "en": "en description"}', 'INACTIVE', '2022-06-02', '2-default', '2-short', '2-long',
        '2-welcome', '2-dropdown');

INSERT INTO TOPIC (`id`, `tenant_id`, `name`, `description`, `status`, `create_date`, `titles_default`, `titles_short`,
                   `titles_long`, `titles_welcome`, `titles_dropdown`)
VALUES (3, '2', '{"de" : "de another topic"}', '{"de" : "de description"}', 'ACTIVE', '2022-06-02', '3-default',
        '3-short', '3-long', '3-welcome', '3-dropdown');

CREATE TABLE IF NOT EXISTS topic_group
(
    `id`          bigint(21)   NOT NULL,
    `name`        varchar(100) NOT NULL,
    `create_date` datetime     NOT NULL,
    `tenant_id`   bigint(21)   NULL,
    `update_date` datetime     NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE SEQUENCE sequence_topic_group
    INCREMENT BY 1
    START WITH 1;

CREATE TABLE IF NOT EXISTS topic_group_x_topic
(
    group_id bigint(21) NOT NULL,
    topic_id bigint(21) NOT NULL,
    foreign key (group_id) references topic_group (id) ON UPDATE CASCADE ON DELETE CASCADE,
    foreign key (topic_id) references topic (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE SEQUENCE sequence_topic_group_x_topic
    INCREMENT BY 1
    START WITH 1;