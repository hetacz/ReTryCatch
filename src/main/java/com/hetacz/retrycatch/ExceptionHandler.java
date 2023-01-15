package com.hetacz.retrycatch;

@FunctionalInterface
public interface ExceptionHandler {

    void handleException(Exception e);
}
