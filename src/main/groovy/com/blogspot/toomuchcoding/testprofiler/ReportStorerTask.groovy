package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.PackageScope
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.gradle.api.Project

@PackageScope
@CompileStatic
@Slf4j
class ReportStorerTask {

    protected static final Closure<String> DEFAULT_ROW_FROM_REPORT_CONVERTER = { TestProfilerPluginExtension testProfilerPluginExtension , ReportRow reportRow ->
        "${reportRow.module}${testProfilerPluginExtension.separator}${reportRow.testExecutionResult.testClassName}${testProfilerPluginExtension.separator}${reportRow.testExecutionResult.testName}${testProfilerPluginExtension.separator}${reportRow.testExecutionResult.testExecutionTime}${testProfilerPluginExtension.separator}${reportRow.testClassExecutionTime}".toString()
    }

    private final TestProfilerPluginExtension testProfilerPluginExtension
    private final Project project

    ReportStorerTask(TestProfilerPluginExtension testProfilerPluginExtension, Project project) {
        this.testProfilerPluginExtension = testProfilerPluginExtension
        this.project = project
    }

    public void storeReport(Set<TestExecutionResult> testExecutionResults) {
        log.debug("All test execution results [$testExecutionResults]")
        File report = createNewReportFile()
        addHeadersToFile(report)
        Map<String, Double> classExecutionTime = calculateClassExecutionTime(testExecutionResults)
        log.debug("Calculated class execution time [$classExecutionTime]")
        String testExecutionResult = buildTestExecutionResult(classExecutionTime, testExecutionResults)
        log.debug("Test execution result [$testExecutionResult]")
        appendTestExecutionResultToFile(report, testExecutionResult)
        println "Your tests report is ready at [$report.absolutePath]"
        appendTestExecutionResultToMergedTestSummary(testExecutionResult)
    }

    private File createNewReportFile() {
        File report = new File(project.buildDir, testProfilerPluginExtension.relativeReportPath.toString())
        log.debug("Creating a new file [$report]")
        report.delete()
        report.parentFile.mkdirs()
        report.createNewFile()
        return report
    }

    private File addHeadersToFile(File report) {
        return report << testProfilerPluginExtension.outputReportHeaders
    }

    private File appendTestExecutionResultToFile(File report, String testExecutionResult) {
        return report << testExecutionResult
    }

    private void appendTestExecutionResultToMergedTestSummary(String testExecutionResult) {
        File mergedTestProfilingSummary = testProfilerPluginExtension.mergedSummaryPath
        mergedTestProfilingSummary.parentFile.mkdirs()
        mergedTestProfilingSummary << testExecutionResult << '\n'
        log.debug("Stored [$testExecutionResult] in [$mergedTestProfilingSummary]")
    }

    private String buildTestExecutionResult(Map<String, Double> classExecutionTime, Set<TestExecutionResult> testExecutionResults) {
        return testExecutionResults.collect {
            new ReportRow(project.path, it, classExecutionTime[it.testClassName])
        }.sort(testProfilerPluginExtension.comparator)
        .collect(rowFromReport()).join('\n')
    }

    private Map<String, Double> calculateClassExecutionTime(Set<TestExecutionResult> testExecutionResults) {
        return testExecutionResults.groupBy {
            TestExecutionResult testExecutionResult -> testExecutionResult.testClassName
        }.collectEntries {
            [it.key, (it.value.sum { TestExecutionResult testExecutionResult -> testExecutionResult.testExecutionTime } as Double).round(3)]
        } as Map<String, Double>
    }

    Closure<String> rowFromReport() {
        return testProfilerPluginExtension.rowFromReport.curry(testProfilerPluginExtension)
    }
}
