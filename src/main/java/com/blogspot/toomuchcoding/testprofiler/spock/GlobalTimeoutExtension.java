package com.blogspot.toomuchcoding.testprofiler.spock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.extension.builtin.TimeoutInterceptor;
import org.spockframework.runtime.model.SpecInfo;

public class GlobalTimeoutExtension implements IGlobalExtension {

    private static final Logger log = LoggerFactory.getLogger(GlobalTimeoutExtension.class);

    public void start() {

    }

    @Override
    public void visitSpec(SpecInfo spec) {
        final Integer defaultTestTimeout = Integer.valueOf(System.getProperty("default.test.timeout", "9999"));
        log.info("Applying global timeout extension with value [" + defaultTestTimeout + "]");
        spec.getInterceptors().add(new TimeoutInterceptor(new CustomTimeout(defaultTestTimeout)));
    }

    public void stop() {

    }

}
