package com.blogspot.toomuchcoding.testprofiler
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@PackageScope
@CompileStatic
@Slf4j
class ReportMergerTask extends DefaultTask {

    TestProfilerPluginExtension testProfilerPluginExtension
    @OutputFile File mergedTestProfilingSummaryFile

    @TaskAction
    void testsProfileSummaryReport() {
        log.debug("Will store merged test profiling summary in [${getMergedTestProfilingSummaryFile()}]")
        Project rootProject = project.rootProject
        String fileContent = rootProject.getAllprojects().collect {
            File report = new File(it.buildDir, getTestProfilerPluginExtension().relativeReportPath.toString())
            log.debug("Report to collect [$report]")
            return report
        }.findAll {
            it.exists()
        }.collect {
            it.text - getTestProfilerPluginExtension().outputReportHeaders
        }.join('\n')
        if (!fileContent) {
            log.info("The reports were empty - sth went wrong")
            return
        }
        storeCollectedReport(fileContent)
    }

    private void storeCollectedReport(String fileContent) {
        log.info("Saving file [${getMergedTestProfilingSummaryFile()}] content [$fileContent]")
        getMergedTestProfilingSummaryFile().text = getTestProfilerPluginExtension().outputReportHeaders
        Set<ReportRow> reportRows = new TreeSet<ReportRow>(getTestProfilerPluginExtension().comparator as Comparator<ReportRow>)
        appendReportRow(fileContent, reportRows)
        getMergedTestProfilingSummaryFile() << reportRows.collect(rowFromReport()).join('\n')
        println "Your combined report is available here [${getMergedTestProfilingSummaryFile()}]"
    }

    private void appendReportRow(String fileContent, Set<ReportRow> reportRows) {
        fileContent.split('\n')
                .each { String string ->
            String[] row = string.split(getTestProfilerPluginExtension().separator)
            log.debug("Converting row $row")
            try {
                reportRows << new ReportRow(row[0], new TestExecutionResult(row[1], row[2], row[3] as Double), row[4] as Double)
            } catch (NumberFormatException e) {
                log.warn("Exception occurred while trying to parse a report row", e)
            }
        }
    }

    Closure<String> rowFromReport() {
        return getTestProfilerPluginExtension().rowFromReport.curry(getTestProfilerPluginExtension())
    }
}
