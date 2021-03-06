# Transactional-outbox-module

This is a basic polling publisher implementation of the transactional-outbox pattern.

## Middleware

- MySQL
- RabbitMQ

## Implementation

To use this module, you only use following classes:

- com.curtisnewbie.module.outbox.publisher.TransactionalMessagePublisher
    - for publishing messages
- com.curtisnewbie.module.outbox.consumer.DuplicateMessageTracker
    - for tracking duplicate messages

`TransactionalMessagePublisher` is used to publish messages, the messages that are passed in are not dispatched to message broker immediately, instead these messages are stored in table `message`. To publish a transactional message, simply wrap the `TransactionalMessagePublisher.publish(...)` method with the other business methods within the same transaction. 

The `MessagePoller` is responsible for polling the unpublished messages from database, it's just like a task producer. The workers are the `PublishingWorker` objects, that take messages from the `MessagePoller` and actually dispatch messages to message broker. These workers are simply `Runnable`, and the number of them can be configured.

Messages may be dispatched multiple times, so the consumers may want to drop the duplicate messages or make the method idempotent. `DuplicateMessageTracker` is used just for this, in essence, it use the table `consumed_message` to remember the messages that it has seen. 

Note that there is only one `MessagePoller` for polling messages from database, but there can be N `PublishingWorker` for dispatching messages, this module internally uses a `BlockingQueue` for this, when the queue is full, it blocks the `MessagePoller`.

## Externalized Configuration

The following are the properties that you may configure:

data type | property name | description | default value
--------- | ------------- | ----------- | -------------
int | transactional-outbox-module.publishing-concurrency | number of `PublishingWorker` used | 4 
int | transactional-outbox-module.message-polling-page-size | page size of unpublished messages polled by `MessagePoller` from database | 30
int | transactional-outbox-module.message-polling-total-limit  | total size limit of all messages polled and stored in application (i.e., the size of the `BlockingQueue`) | 200
int | transactional-outbox-module.message-polling-wait-time | time duration in milliseconds that the `MessagePoller` will wait for next time polling messages from database  | 100

## Dependencies

This project depends on the following modules that you must manually install (using `mvn clean install`).

- messaging-module
    - description: for RabbitMQ-based messaging 
    - url: https://github.com/CurtisNewbie/messaging-module
    - branch: main


