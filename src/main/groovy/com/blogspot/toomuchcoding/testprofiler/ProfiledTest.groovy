package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestListener

import java.util.concurrent.ConcurrentHashMap

@PackageScope
@CompileStatic
class ProfiledTest extends Test {

    private final Set<TestExecutionResult> testExecutionResults = Collections.newSetFromMap(new ConcurrentHashMap<TestExecutionResult, Boolean>())
    private final TestProfilerPluginExtension testProfilerPluginExtension

    ProfiledTest(TestProfilerPluginExtension testProfilerPluginExtension) {
        this.testProfilerPluginExtension = testProfilerPluginExtension
        doFirst {
            testExecutionResults.clear()
        }
        addTestListener(new TestExecutionResultSavingTestListener(testExecutionResults))
        doLast {
            new ReportStorer(testProfilerPluginExtension, project).storeReport(testExecutionResults)
        }
    }

    protected ProfiledTest(TestProfilerPluginExtension testProfilerPluginExtension,
                               TestListener testListener,
                               ReportStorer reportStorer) {
        this.testProfilerPluginExtension = testProfilerPluginExtension
        doFirst {
            testExecutionResults.clear()
        }
        addTestListener(testListener)
        doLast {
            reportStorer.storeReport(testExecutionResults)
        }
    }

}
