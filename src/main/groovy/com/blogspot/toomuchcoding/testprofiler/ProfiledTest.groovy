package com.blogspot.toomuchcoding.testprofiler
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.testing.Test

import java.util.concurrent.ConcurrentHashMap

@PackageScope
@CompileStatic
@Slf4j
class ProfiledTest extends Test {

    TestProfilerPluginExtension testProfilerPluginExtension
    @OutputDirectory File reportDir
    @OutputDirectory File mergedTestProfilingSummaryDir

    ProfiledTest() {
        Set<TestExecutionResult> testExecutionResults = Collections.newSetFromMap(new ConcurrentHashMap<TestExecutionResult, Boolean>())
        addTestListener(new TestExecutionResultSavingTestListener(testExecutionResults))
        doLast storeReport(testExecutionResults)
    }

    private Closure storeReport(Set<TestExecutionResult> testExecutionResults) {
        return {
            log.debug("Stored results are $testExecutionResults")
            new ReportStorer(getTestProfilerPluginExtension(), project, getReportDir(), getMergedTestProfilingSummaryDir()).storeReport(testExecutionResults)
        }
    }

}
