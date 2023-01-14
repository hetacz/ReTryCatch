package com.hetacz.retrycatch;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

@Test
@Slf4j
public class FailTest {

    private static final int RETRIES = 3;
    private static final int TOO_BIG_CONST = 100;
    private static final String RETRIED_AND_FAILED = "Retried and failed!";
    private static final String IN_THE_CALL = "In the `call()` method. The counter is: {}, should be: {}.";
    private static final String NOT_YET = "Not yet.";
    private static final String RECOVERING = "Recovering... Incrementing the counter: {}.";

    public void failRetryTest() {
        var flag = Retry.retry(RETRIES, new RetryableWithResult<>() {

            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public void handleException(Exception e) {
                log.error(RETRIED_AND_FAILED);
            }

            @Override
            public Integer call() {
                log.info(IN_THE_CALL, counter.incrementAndGet(), TOO_BIG_CONST);
                if (counter.get() == TOO_BIG_CONST) {
                    return counter.get();
                } else {
                    throw new RuntimeException(NOT_YET);
                }
            }
        });
        Assert.assertFalse(flag.isPresent());
    }

    public void failRetryWithRecoverTest() {
        var flag = Retry.retryWithRecover(RETRIES, new RecoverableWithResult<>() {

            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public void recover() {
                log.info(RECOVERING, counter.incrementAndGet());
            }

            @Override
            public void handleException(Exception e) {
                log.error(RETRIED_AND_FAILED);
            }

            @Override
            public Integer call() {
                log.info(IN_THE_CALL, counter.get(), TOO_BIG_CONST);
                if (counter.get() == TOO_BIG_CONST) {
                    return counter.get();
                } else {
                    throw new RuntimeException(NOT_YET);
                }
            }
        });
        Assert.assertFalse(flag.isPresent());
    }

    public void testNoValueEverReturned() {
        var flag = Retry.retryWithRecover(RETRIES, new RecoverableWithResult<>() {

            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public void recover() {
                log.info(RECOVERING, counter.incrementAndGet());
            }

            @Override
            public void handleException(Exception e) {
                log.error(RETRIED_AND_FAILED);
            }

            @Override
            public Integer call() {
                if (counter.get() == TOO_BIG_CONST) {
                    return 42;
                } else {
                    throw new RuntimeException(NOT_YET);
                }
            }
        });
        Assert.assertFalse(flag.isPresent());
    }
}
