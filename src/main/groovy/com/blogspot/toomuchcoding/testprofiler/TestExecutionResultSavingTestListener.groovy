package com.blogspot.toomuchcoding.testprofiler
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.gradle.api.Project
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult

@PackageScope
@CompileStatic
class TestExecutionResultSavingTestListener implements TestListener {

    private static final String DEFAULT_WARN_MSG = "[TEST-PROFILER] For project [%s] test [%s] from class [%s] took too long to run. Test execution time [%s]. Threshold [%s]."

    private final Set<TestExecutionResult> testExecutionResults
    private final TestProfilerPluginExtension testProfilerPluginExtension
    private final LoggerProxy loggerProxy
    private final Project project

    TestExecutionResultSavingTestListener(Set<TestExecutionResult> testExecutionResults,
                                          TestProfilerPluginExtension testProfilerPluginExtension,
                                          Project project,
                                          LoggerProxy loggerProxy = new LoggerProxy()) {
        this.testExecutionResults = testExecutionResults
        this.testProfilerPluginExtension = testProfilerPluginExtension
        this.loggerProxy = loggerProxy
        this.project = project
    }

    @Override
    void beforeSuite(TestDescriptor suite) { }

    @Override
    void afterSuite(TestDescriptor suite, TestResult result) { }

    @Override
    void beforeTest(TestDescriptor testDescriptor) { }

    @Override
    void afterTest(TestDescriptor testDescriptor, TestResult result) {
        if (!testProfilerPluginExtension.enabled) {
            loggerProxy.debug("The plugin is disabled so no test results will be recorded")
            return
        }
        long executionTimeInMs = result.endTime - result.startTime
        TestExecutionResult testExecutionResult = new TestExecutionResult(testDescriptor.className, testDescriptor.name, (executionTimeInMs / 1000) as Double)
        loggerProxy.info("[TEST-PROFILER] Gathered Test Execution Result [$testExecutionResult] with result [$result]")
        if (minThresholdIsNotSet() || testExecutionTimeIsBelowMinThreshold(executionTimeInMs)) {
            loggerProxy.debug("Min threshold equals [$testProfilerPluginExtension.minTestThreshold] ms and execution time [$executionTimeInMs] ms. Will store the result in the report")
            storeResult(testExecutionResult)
        }
        if (testExecutionTimeIsBelowMaxThreshold(executionTimeInMs)) {
            loggerProxy.debug("Test execution time [$executionTimeInMs] ms if above the max threshold [$testProfilerPluginExtension.minTestThreshold]. Will perform additional logic")
            performAdditionalLogic(testDescriptor, testExecutionResult, executionTimeInMs)
        }
    }

    private void storeResult(TestExecutionResult testExecutionResult) {
        testExecutionResults << testExecutionResult
    }

    private boolean minThresholdIsNotSet() {
        return testProfilerPluginExtension.minTestThreshold == null
    }

    private boolean testExecutionTimeIsBelowMinThreshold(long executionTimeInMs) {
        return executionTimeInMs >= testProfilerPluginExtension.minTestThreshold
    }

    private boolean testExecutionTimeIsBelowMaxThreshold(long executionTimeInMs) {
        return executionTimeInMs >= testProfilerPluginExtension.buildBreakerOptions.maxTestThreshold
    }

    private void performAdditionalLogic(TestDescriptor testDescriptor, TestExecutionResult testExecutionResult, long executionTimeInMs) {
        TestProfilerPluginExtension.BuildBreakerOptions buildBreakerOptions = testProfilerPluginExtension.buildBreakerOptions
        TestProfilerPluginExtension.BuildBreakerOptions.WhatToDo whatToDo = buildBreakerOptions.ifTestsExceedMaxThreshold.whatToDo
        Closure<String> action = whatToDo.action
        if (buildBreakerOptions.shouldDisplayWarning()) {
            loggerProxy.warn( whatToDo.doNothing() ?
                    String.format(DEFAULT_WARN_MSG, project.path, testExecutionResult.testName, testExecutionResult.testClassName, executionTimeInMs, testProfilerPluginExtension.buildBreakerOptions.maxTestThreshold) :
                    action.curry(testDescriptor, testExecutionResult).call() as String)
        } else if (buildBreakerOptions.shouldAct()) {
            loggerProxy.debug("Test execution time was exceeded - will perform custom logic defined by the user")
            action.curry(loggerProxy, testDescriptor, testExecutionResult).call()
        }
    }
}
