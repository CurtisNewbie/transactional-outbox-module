package com.curtisnewbie.module.outbox.publisher;

import com.curtisnewbie.module.outbox.service.MessageOutboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Factory of {@link PublishingWorker}
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class PublishingWorkerFactory {

    private ExecutorService executor;
    private static final String PROP_KEY_WORKER_COUNT = "transactional-outbox-module.publishing-concurrency";

    @Value("${" + PROP_KEY_WORKER_COUNT + ":4}")
    private int workers;

    @Autowired
    private MessageOutboxService messageOutboxService;
    @Autowired
    private MessagePoller messagePoller;

    @PostConstruct
    public void onInit() {
        // one poller, N workers
        log.info("Creating {} workers for sending messages", workers);
        executor = Executors.newFixedThreadPool(workers);
        for (int i = 0; i < workers; i++) {
            executor.execute(new PublishingWorker(messagePoller, messageOutboxService));
        }
    }

    @PreDestroy
    public void preDestroy() {
        PublishingWorker.notifyApplicationShutdown();
        executor.shutdown();
    }
}
