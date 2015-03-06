package com.blogspot.toomuchcoding.testprofiler

import org.gradle.api.Project
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import spock.lang.Specification
import spock.lang.Unroll

class TestExecutionResultSavingTestListenerSpec extends Specification {

    Project project = Stub()
    TestDescriptor testDescriptor = Stub()
    LoggerProxy loggerProxy = Mock()

    @Unroll
    def "should store [#expectedStoredElements] results if min threshold is [#minThreshold]"() {
        given:
            TestResult testResult = stubbedTestResult()
        and:
            Set<TestExecutionResult> results = []
            TestExecutionResultSavingTestListener listener = new TestExecutionResultSavingTestListener(results, new TestProfilerPluginExtension(minTestThreshold: minThreshold), project)
        when:
            listener.afterTest(testDescriptor, testResult)
        then:
            results.size() == expectedStoredElements
        where:
            minThreshold || expectedStoredElements
            10           || 0
            0            || 1
    }

    def 'should display warning if test time execution was too long and user selected that option'() {
        given:
            TestResult testResult = stubbedTestResult()
        and:
            TestProfilerPluginExtension extension = new TestProfilerPluginExtension()
            extension.buildBreaker {
                maxTestThreshold = 1
                ifTestsExceedMaxThreshold {
                    displayWarning()
                }
            }
        and:
            Set<TestExecutionResult> results = []
            TestExecutionResultSavingTestListener listener = new TestExecutionResultSavingTestListener(results, extension, project, loggerProxy)
        when:
            listener.afterTest(testDescriptor, testResult)
        then:
            1 * loggerProxy.warn({ it.contains('took too long to run')})
    }

    def 'should display warning with custom message if test time execution was too long and user selected that option'() {
        given:
            TestResult testResult = stubbedTestResult()
        and:
            TestProfilerPluginExtension extension = new TestProfilerPluginExtension()
            extension.buildBreaker {
                maxTestThreshold = 1
                ifTestsExceedMaxThreshold {
                    displayWarning { TestDescriptor testDescriptor, TestExecutionResult testExecutionResult ->
                        'some text'
                    }
                }
            }
        and:
            Set<TestExecutionResult> results = []
            TestExecutionResultSavingTestListener listener = new TestExecutionResultSavingTestListener(results, extension, project, loggerProxy)
        when:
            listener.afterTest(testDescriptor, testResult)
        then:
            1 * loggerProxy.warn({ it == 'some text'})
    }

    def 'should perform custom logic if test time execution was too long and user selected option to act'() {
        given:
            TestResult testResult = stubbedTestResult()
        and:
            TestProfilerPluginExtension extension = new TestProfilerPluginExtension()
            boolean closureHasBeenCalled = false
            extension.buildBreaker {
                maxTestThreshold = 1
                ifTestsExceedMaxThreshold {
                    act { LoggerProxy log, TestDescriptor testDescriptor, TestExecutionResult testExecutionResult ->
                        closureHasBeenCalled = true
                    }
                }
            }
        and:
            Set<TestExecutionResult> results = []
            TestExecutionResultSavingTestListener listener = new TestExecutionResultSavingTestListener(results, extension, project, loggerProxy)
        when:
            listener.afterTest(testDescriptor, testResult)
        then:
            closureHasBeenCalled
    }

    private TestResult stubbedTestResult() {
        TestResult testResult = Stub()
        testResult.endTime >> 3
        testResult.startTime >> 1
        return testResult
    }

}
