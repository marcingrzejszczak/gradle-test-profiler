package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.PackageScope
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult

@PackageScope
@CompileStatic
@Slf4j
class TestExecutionResultSavingTestListener implements TestListener {

    private final Set<TestExecutionResult> testExecutionResults
    private final TestProfilerPluginExtension testProfilerPluginExtension

    TestExecutionResultSavingTestListener(Set<TestExecutionResult> testExecutionResults,
                                          TestProfilerPluginExtension testProfilerPluginExtension) {
        this.testExecutionResults = testExecutionResults
        this.testProfilerPluginExtension = testProfilerPluginExtension
    }

    @Override
    void beforeSuite(TestDescriptor suite) { }

    @Override
    void afterSuite(TestDescriptor suite, TestResult result) { }

    @Override
    void beforeTest(TestDescriptor testDescriptor) { }

    @Override
    void afterTest(TestDescriptor testDescriptor, TestResult result) {
        long executionTimeInMs = result.endTime - result.startTime
        TestExecutionResult testExecutionResult = new TestExecutionResult(testDescriptor.className, testDescriptor.name, (executionTimeInMs / 1000) as Double)
        log.info("[TEST-PROFILER] Gathered Test Execution Result [$testExecutionResult] with result [$result]")
        storeResultIfAboveMinThreshold(executionTimeInMs, testExecutionResult)
    }

    private void storeResultIfAboveMinThreshold(Long executionTimeInMs, TestExecutionResult testExecutionResult) {
        if (executionTimeInMs >= testProfilerPluginExtension.minTestThreshold) {
            testExecutionResults << testExecutionResult
        }
    }
}
