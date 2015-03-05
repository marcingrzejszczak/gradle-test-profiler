package com.blogspot.toomuchcoding.testprofiler.spock
import groovy.util.logging.Slf4j
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.extension.builtin.TimeoutInterceptor
import org.spockframework.runtime.model.SpecInfo

@Slf4j
class GlobalTimeoutExtension implements IGlobalExtension {

    void start() {

    }

    @Override
    void visitSpec(SpecInfo spec) {
        final Integer defaultTestTimeout = System.getProperty('default.test.timeout', '9999').toInteger()
        log.info("Applying global timeout extension with value [$defaultTestTimeout]")
        spec.getInterceptors().add(new TimeoutInterceptor(new CustomTimeout(defaultTestTimeout)))
    }

    void stop() {

    }
}
