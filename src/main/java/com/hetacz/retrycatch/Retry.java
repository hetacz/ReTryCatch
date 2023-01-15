package com.hetacz.retrycatch;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@UtilityClass
public class Retry {

    private final String INFO_TEXT = "Trying to recover...";
    private final String WARN_TEXT = "Error while performing action {}, attempt: {}.";
    private final String ERR_TEXT = "{} failed, out of retry attempts, tried {} times.";
    private final String FAIL = "Out of retry attempts, tried {} times with no success.";

    public void retry(int retries, Retryable retryable) {
        for (int i = 0; i < retries; i++) {
            try {
                retryable.run();
                break;
            } catch (Exception e) {
                lastChanceFailed(retryable, retries, i, e);
            }
        }
    }

    public void retryWithRecover(int retries, Recoverable recoverable) {
        for (int i = 0; i < retries; i++) {
            try {
                recoverable.run();
                break;
            } catch (Exception e) {
                recover(recoverable, retries, i, e);
            }
        }
    }

    public <V> Optional<V> retry(int retries, RetryableWithResult<? extends V> retryable) {
        for (int i = 0; i < retries; i++) {
            try {
                return Optional.of(retryable.call());
            } catch (Exception e) {
                lastChanceFailed(retryable, retries, i, e);
            }
        }
        log.warn(FAIL, retries);
        return Optional.empty();
    }

    public <V> Optional<V> retryWithRecover(int retries, RecoverableWithResult<? extends V> recoverable) {
        for (int i = 0; i < retries; i++) {
            try {
                return Optional.of(recoverable.call());
            } catch (Exception e) {
                recover(recoverable, retries, i, e);
            }
        }
        log.warn(FAIL, retries);
        return Optional.empty();
    }

    private <R extends Recoverer> void recover(R recoverable, int retries, int i, Exception e) {
        log.warn(WARN_TEXT, recoverable, i + 1, e);
        if (i == retries - 1) {
            handleException((ExceptionHandler) recoverable, retries, e);
        } else {
            log.info(INFO_TEXT);
            recoverable.recover();
        }
    }

    private <H extends ExceptionHandler> void lastChanceFailed(H handleException, int retries, int i, Exception e) {
        log.warn(WARN_TEXT, handleException, i + 1, e);
        if (i == retries - 1) {
            handleException(handleException, retries, e);
        }
    }

    private <H extends ExceptionHandler> void handleException(H handleException, int retries, Exception e) {
        log.error(ERR_TEXT, handleException, retries, e);
        handleException.handleException(e);
    }
}
