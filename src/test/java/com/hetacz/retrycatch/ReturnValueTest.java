package com.hetacz.retrycatch;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

@Test
@Slf4j
public class ReturnValueTest {

    private static final int RETRIES = 3;
    private static final int CONST = 2;
    private static final String RETRIED_AND_FAILED = "Retried and failed!";
    private static final String NOT_YET = "Not yet.";
    private static final String IN_THE_CALL_METHOD_THE_COUNTER_IS_SHOULD_BE =
            "In the `call()` method. The counter is: {}, should be: {}.";
    private static final String IN_THE_CALL_METHOD_THE_COUNTER_IS = "In the `call()` method. The counter is: {}.";
    private static final String RECOVERING_INCREMENTING_COUNTER = "Recovering... Incrementing counter: {}.";

    public void testReturnValue() {

        var flag = Retry.retry(RETRIES, new RetryableWithResult<>() {

            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public void handleException(Exception e) {
                log.error(RETRIED_AND_FAILED);
            }

            @Override
            public Integer call() {
                log.info(IN_THE_CALL_METHOD_THE_COUNTER_IS_SHOULD_BE, counter.getAndIncrement(), CONST);
                if (counter.get() == CONST) {
                    return counter.get();
                } else {
                    throw new RuntimeException(NOT_YET);
                }
            }
        });
        Assert.assertEquals(flag.orElse(null), CONST);
    }

    public void testRecoverAndReturnValue() {

        var flag = Retry.retryWithRecover(RETRIES, new RecoverableWithResult<>() {

            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public void recover() {
                log.info(RECOVERING_INCREMENTING_COUNTER, counter.incrementAndGet());
            }

            @Override
            public void handleException(Exception e) {
                log.error(RETRIED_AND_FAILED);
            }

            @Override
            public Integer call() {
                log.info(IN_THE_CALL_METHOD_THE_COUNTER_IS, counter.get());
                if (counter.get() == CONST) {
                    return counter.get();
                } else {
                    throw new RuntimeException(NOT_YET);
                }
            }
        });
        Assert.assertEquals(flag.orElse(null), CONST);
    }
}
