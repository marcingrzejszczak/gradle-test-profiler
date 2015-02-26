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

    TestExecutionResultSavingTestListener(Set<TestExecutionResult> testExecutionResults) {
        this.testExecutionResults = testExecutionResults
    }

    @Override
    void beforeSuite(TestDescriptor suite) { }

    @Override
    void afterSuite(TestDescriptor suite, TestResult result) { }

    @Override
    void beforeTest(TestDescriptor testDescriptor) { }

    @Override
    void afterTest(TestDescriptor testDescriptor, TestResult result) {
        TestExecutionResult testExecutionResult = new TestExecutionResult(testDescriptor.className, testDescriptor.name, ((result.endTime - result.startTime) / 1000) as Double)
        log.debug("Gathered Test Execution Result [$testExecutionResult]")
        testExecutionResults << testExecutionResult
    }
}
