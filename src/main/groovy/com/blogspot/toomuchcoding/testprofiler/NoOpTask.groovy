package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.PackageScope
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

@PackageScope
@CompileStatic
class NoOpTask extends DefaultTask {
    private final LoggerProxy loggerProxy

    NoOpTask() {
        this.loggerProxy = new LoggerProxy()
    }

    @TaskAction
    void logThatTaskIsDisabled() {
        loggerProxy.info("Task is disabled - please enable it via the extension")
    }
}
