package com.hetacz.retrycatch;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

@Test
@Slf4j
public class RetryTest {

    private static final int RETRIES = 3;
    private static final int CONST = 2;
    private static final String RETRIED_AND_FAILED = "Retried and failed!";
    private static final String NOT_YET = "Not yet.";
    private static final String RECOVERING_SETTING_FLAG_TO = "Recovering... Setting flag to {}.";
    private static final String RECOVERING_COUNTER_IS_NOW = "Recovering... Counter is now: {}.";
    private static final String IN_THE_RUN_METHOD = "In the `run()` method.";
    private static final String IN_THE_RUN_METHOD_FLAG = "In the `run()` method. The flag is: {}.";
    private static final String IN_THE_RUN_METHOD_THE_COUNTER =
            "In the `run()` method. The counter is: {}, should be: {}.";

    public void testSuccessInstant() {

        Retry.retry(RETRIES, new Retryable() {

            @Override
            public void handleException(Exception e) {
                log.error(RETRIED_AND_FAILED);
            }

            @Override
            public void run() {
                log.info(IN_THE_RUN_METHOD);
                Assert.assertTrue(true);
            }
        });
    }

    public void testSuccessAfterReties() {

        Retry.retry(RETRIES, new Retryable() {

            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public void handleException(Exception e) {
                log.error(RETRIED_AND_FAILED);
            }

            @Override
            public void run() {
                log.info(IN_THE_RUN_METHOD_THE_COUNTER, counter.getAndIncrement(), CONST);
                if (counter.get() == CONST) {
                    Assert.assertEquals(counter.get(), CONST);
                } else {
                    throw new RuntimeException(NOT_YET);
                }
            }
        });
    }

    public void testSuccessAfterRecover() {

        Retry.retryWithRecover(RETRIES, new Recoverable() {

            private boolean flag = false;

            @Override
            public void recover() {
                flag = true;
                log.info(RECOVERING_SETTING_FLAG_TO, flag);
            }

            @Override
            public void handleException(Exception e) {
                log.error(RETRIED_AND_FAILED);
            }

            @Override
            public void run() {
                log.info(IN_THE_RUN_METHOD_FLAG, flag);
                if (flag) {
                    Assert.assertTrue(flag);
                } else {
                    throw new RuntimeException(NOT_YET);
                }
            }
        });
    }

    public void testSuccessAfterRecoveringFewTimes() {

        Retry.retryWithRecover(RETRIES, new Recoverable() {

            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public void recover() {
                log.info(RECOVERING_COUNTER_IS_NOW, counter.incrementAndGet());
            }

            @Override
            public void handleException(Exception e) {
                log.error(RETRIED_AND_FAILED);
            }

            @Override
            public void run() {
                log.info(IN_THE_RUN_METHOD_THE_COUNTER, counter.get(), CONST);
                if (counter.get() == CONST) {
                    Assert.assertEquals(counter.get(), CONST);
                } else {
                    throw new RuntimeException(NOT_YET);
                }
            }
        });
    }
}
