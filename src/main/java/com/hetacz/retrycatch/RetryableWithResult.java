package com.hetacz.retrycatch;

import java.util.concurrent.Callable;

public interface RetryableWithResult<V> extends ExceptionHandler, Callable<V> {

}
