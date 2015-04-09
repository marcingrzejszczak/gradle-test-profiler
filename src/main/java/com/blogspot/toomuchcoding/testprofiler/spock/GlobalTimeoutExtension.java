package com.blogspot.toomuchcoding.testprofiler.spock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.extension.builtin.TimeoutInterceptor;
import org.spockframework.runtime.model.SpecInfo;

import java.util.Arrays;
import java.util.List;

public class GlobalTimeoutExtension implements IGlobalExtension {

    private static final Logger log = LoggerFactory.getLogger(GlobalTimeoutExtension.class);

    public void start() {

    }

    @Override
    public void visitSpec(SpecInfo spec) {
        final Integer defaultTestTimeout = Integer.valueOf(System.getProperty("default.test.timeout", "9999"));
        String testClassesToIgnoreProp = System.getProperty("test.classes.to.ignore", "");
        List<String> testClassesToIgnore = Arrays.asList(testClassesToIgnoreProp.split(","));
        log.info("Applying global timeout extension with value [" + defaultTestTimeout + "] " +
                "for classes not matching regexp [" + testClassesToIgnore + "]");
        boolean specNameMatchesRegexp = checkIfSpecNameMatchesRegexpToIgnore(spec, testClassesToIgnore);
        if (specNameMatchesRegexp) {
            log.info("Spec with name [" + spec.getName() + "] will be ignored since it matches the regexp");
            return;
        }
        spec.getInterceptors().add(new TimeoutInterceptor(new CustomTimeout(defaultTestTimeout)));
    }

    private boolean checkIfSpecNameMatchesRegexpToIgnore(SpecInfo spec, List<String> testClassesToIgnore) {
        boolean specNameMatchesRegexp = false;
        for(String testClassRegexp : testClassesToIgnore) {
            if(spec.getDescription().getClassName().matches(testClassRegexp)) {
                specNameMatchesRegexp = true;
                break;
            }
        }
        return specNameMatchesRegexp;
    }

    public void stop() {

    }

}
