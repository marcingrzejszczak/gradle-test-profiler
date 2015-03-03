package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.CompileStatic
import groovy.transform.ToString

@CompileStatic
@ToString(includeNames = true)
class TestProfilerPluginExtension {

    /**
     * Should TestProfilerPlugin be enabled? If set to false there will be no modification of any Gradle Test classes
     * and the task will simply print a message
     */
    boolean enabled = true

    /**
     * Separator of columns in the output report
     */
    String separator = '\t'

    /**
     * Headers in the report
     */
    String outputReportHeaders = "module${separator}test class name${separator}test name${separator}test execution time in [s]${separator}test class execution time in [s]\n"

    /**
     * Closure that will be converted to a Comparator to compare row entries
     */
    Closure<Integer> comparator = DefaultTestExecutionComparator.DEFAULT_TEST_EXECUTION_COMPARATOR

    /**
     * Closure that converts a reporter row entry to a single String
     */
    Closure<String> rowFromReport = ReportStorer.DEFAULT_ROW_FROM_REPORT_CONVERTER

    /**
     * Path to the report for a module. Defaults to {@code project.buildDir/reports/test_profiling/testsProfile.csv}
     */
    File reportPath

    /**
     * Path to the merged summary of reports. Defaults to {@code project.rootProject.buildDir/reports/test_profiling/summary.csv"
     */
    File mergedSummaryPath
}
