package com.blogspot.toomuchcoding.testprofiler.spock
import groovy.transform.CompileStatic
import groovy.transform.ToString
import spock.lang.Timeout

import java.lang.annotation.Annotation
import java.util.concurrent.TimeUnit

@CompileStatic
@ToString(includeFields = true)
class CustomTimeout implements Timeout {

    private final Integer timeout

    CustomTimeout(int timeout) {
        this.timeout = timeout
    }

    @Override
    int value() {
        return timeout
    }

    @Override
    TimeUnit unit() {
        return TimeUnit.MILLISECONDS
    }

    @Override
    Class<? extends Annotation> annotationType() {
        return Timeout
    }
}
