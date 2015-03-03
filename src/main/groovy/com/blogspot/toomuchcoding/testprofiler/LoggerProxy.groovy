package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.PackageScope
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@PackageScope
@CompileStatic
@Slf4j
class LoggerProxy {

    void debug(String message) {
        log.debug(message)
    }

    void info(String message) {
        log.info(message)
    }

}
