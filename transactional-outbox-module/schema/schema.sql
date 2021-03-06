CREATE TABLE IF NOT EXISTS consumed_message (
    message_id VARCHAR(50) PRIMARY KEY COMMENT 'message id',
    consume_time DATETIME NOT NULL COMMENT 'time when the message was consumed'
) ENGINE=InnoDB COMMENT 'Messages that are consumed';

CREATE TABLE IF NOT EXISTS message (
    message_id VARCHAR(50) COMMENT 'message id',
    create_time DATETIME NOT NULL COMMENT 'time when the message was published/created',
    payload VARCHAR(1000) NOT NULL COMMENT 'message body',
    exchange VARCHAR(256) NOT NULL COMMENT 'exchange name',
    routing_key VARCHAR(256) NOT NULL COMMENT 'routing key',
    is_published SMALLINT NOT NULL DEFAULT 0 COMMENT 'whether the message is published, 0-not published, 1-published',
    PRIMARY KEY(is_published, message_id)
) ENGINE=InnoDB COMMENT 'Messages that will be published';

CREATE TABLE IF NOT EXISTS consumed_message_history (
    message_id VARCHAR(50) PRIMARY KEY COMMENT 'message id',
    consume_time DATETIME NOT NULL COMMENT 'time when the message was consumed'
) ENGINE=InnoDB COMMENT 'History messages that are consumed';

CREATE TABLE IF NOT EXISTS message_history (
    message_id VARCHAR(50) COMMENT 'message id',
    create_time DATETIME NOT NULL COMMENT 'time when the message was published/created',
    payload VARCHAR(1000) NOT NULL COMMENT 'message body',
    exchange VARCHAR(256) NOT NULL COMMENT 'exchange name',
    routing_key VARCHAR(256) NOT NULL COMMENT 'routing key',
    is_published SMALLINT NOT NULL DEFAULT 0 COMMENT 'whether the message is published, 0-not published, 1-published',
    PRIMARY KEY(is_published, message_id)
) ENGINE=InnoDB COMMENT 'History messages that are already published';
