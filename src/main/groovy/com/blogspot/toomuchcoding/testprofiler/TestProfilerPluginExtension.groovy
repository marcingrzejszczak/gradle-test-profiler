package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.CompileStatic
import groovy.transform.ToString

@CompileStatic
@ToString
class TestProfilerPluginExtension {
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
     * Base directory where reports will be gathered. The parent of this directory is build dir of the project
     */
    String reportOutputDir = "reports/test_profiling"

    /**
     * Filename of a single report
     */
    String reportOutputCsvFilename = "testsProfile.csv"

    /**
     * Base directory where merged summary of reports will be kept. The parent of directory is the top root project dir
     */
    String mergedSummaryDir = "reports/test_profiling"

    /**
     * Filename of a merged summary of reports
     */
    String mergedSummaryFileName = "summary.csv"
}
