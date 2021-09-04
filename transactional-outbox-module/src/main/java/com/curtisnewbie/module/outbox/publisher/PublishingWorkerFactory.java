package com.curtisnewbie.module.outbox.publisher;

import com.curtisnewbie.module.outbox.config.ModuleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private MessageOutboxService messageOutboxService;
    @Autowired
    private MessagePoller messagePoller;
    @Autowired
    private ModuleConfig moduleConfig;

    @PostConstruct
    public void onInit() {
        final int N = moduleConfig.getPublishingWorkerCount();
        // one poller, N workers
        log.info("Creating {} workers for dispatching messages to message broker", N);
        executor = Executors.newFixedThreadPool(N);
        for (int i = 0; i < N; i++) {
            executor.execute(new PublishingWorker(messagePoller, messageOutboxService));
        }
    }

    @PreDestroy
    public void preDestroy() {
        PublishingWorker.notifyApplicationShutdown();
        executor.shutdown();
    }
}
