package com.blogspot.toomuchcoding.testprofiler

import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import spock.lang.Specification
import spock.lang.Unroll

class TestExecutionResultSavingTestListenerSpec extends Specification {

    @Unroll
    def "should store [#expectedStoredElements] results if min threshold is [#minThreshold]"() {
        given:
            TestResult testResult = Stub()
            testResult.endTime >> 2
            testResult.startTime >> 1
        and:
            TestDescriptor testDescriptor = Stub()
        and:
            Set<TestExecutionResult> results = []
            TestExecutionResultSavingTestListener listener = new TestExecutionResultSavingTestListener(results, new TestProfilerPluginExtension(minTestThreshold: minThreshold))
        when:
            listener.afterTest(testDescriptor, testResult)
        then:
            results.size() == expectedStoredElements
        where:
            minThreshold || expectedStoredElements
            3            || 0
            0            || 1
    }

}
