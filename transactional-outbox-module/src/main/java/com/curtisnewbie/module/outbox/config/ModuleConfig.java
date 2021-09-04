package com.curtisnewbie.module.outbox.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author yongjie.zhuang
 */
@MapperScan("com.curtisnewbie.module.outbox.dao")
@Configuration
public class ModuleConfig {

    private static final String PROP_KEY_WORKER_COUNT = "transactional-outbox-module.publishing-concurrency";
    private static final String PROP_KEY_POLLING_PAGE_SIZE = "transactional-outbox-module.message-polling-page-size";
    private static final String PROP_KEY_POLLED_MESSAGE_TOTAL_LIMIT = "transactional-outbox-module.message-polling-total-limit";
    private static final String PROP_KEY_WAIT_TIME_FOR_EACH_POLLING = "transactional-outbox-module.message-polling-wait-time";

    @Value("${" + PROP_KEY_WORKER_COUNT + ":4}")
    private int workers;

    @Value("${" + PROP_KEY_POLLING_PAGE_SIZE + ":30}")
    private int pollingPageSize;

    @Value("${" + PROP_KEY_POLLED_MESSAGE_TOTAL_LIMIT + ":200}")
    private int pollingMessageTotalLimit;

    @Value("${" + PROP_KEY_WAIT_TIME_FOR_EACH_POLLING + ":100}")
    private int pollingMessageWaitTime;

    public int getPublishingWorkerCount() {
        return workers;
    }

    public int getMessagePollingPageSize() {
        return pollingPageSize;
    }

    public int getMessagePollingTotalLimit() {
        return pollingMessageTotalLimit;
    }

    public int getMessagePollingWaitTime() {
        return pollingMessageWaitTime;
    }
}
