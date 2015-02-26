package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.CompileStatic

@CompileStatic
class TestProfilerPluginExtension {
    String separator = '\t'
    String outputReportHeaders = "module${separator}test class name${separator}test name${separator}test execution time in [s]${separator}test class execution time in [s]\n"
    Closure<Integer> comparator = DefaultTestExecutionComparator.DEFAULT_TEST_EXECUTION_COMPARATOR
    Closure<String> rowFromReport = ReportStorer.DEFAULT_ROW_FROM_REPORT_CONVERTER
    String reportOutputDir = "reports/test_profiling"
    String reportOutputCsvFilename = "testsProfile.csv"
    String mergedSummaryDir = "build/reports/test_profiling"
    String mergedSummaryFileName = "summary.csv"
}
