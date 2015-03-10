package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@CompileStatic
@Slf4j
class LoggerProxy {

    void debug(String message) {
        log.debug(message)
    }

    void info(String message) {
        log.info(message)
    }

    void warn(String message) {
        log.warn(message)
    }

}