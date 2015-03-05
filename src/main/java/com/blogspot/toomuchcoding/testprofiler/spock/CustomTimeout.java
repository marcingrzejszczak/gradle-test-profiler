package com.blogspot.toomuchcoding.testprofiler.spock;

import spock.lang.Timeout;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

public class CustomTimeout implements Timeout {

    private final Integer timeout;

    public CustomTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public int value() {
        return timeout;
    }

    @Override
    public TimeUnit unit() {
        return TimeUnit.MILLISECONDS;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Timeout.class;
    }

    @Override
    public String toString() {
        return "CustomTimeout{" +
                "timeout=" + timeout +
                '}';
    }
}
