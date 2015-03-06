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
    String outputReportHeaders = ['module', 'test class name', 'test name', 'test execution time in [s]', 'test class execution time in [s]'].join(separator).concat('\n')

    /**
     * Closure that will be converted to a Comparator to compare row entries - needed to sort rows
     */
    Closure<Integer> comparator = DefaultTestExecutionComparator.DEFAULT_TEST_EXECUTION_COMPARATOR

    /**
     * Closure that converts a reporter row entry to a single String
     */
    Closure<String> rowFromReport = ReportStorerTask.DEFAULT_ROW_FROM_REPORT_CONVERTER

    /**
     * Path to the report for a module. Defaults to {@code project.buildDir/reports/test_profiling/testsProfile.csv}
     */
    File relativeReportPath

    /**
     * Path to the merged summary of reports. Defaults to {@code project.rootProject.buildDir/reports/test_profiling/summary.csv"
     */
    File mergedSummaryPath

    /**
     * Milliseconds of test execution above which we will store information about the test. Defaults to 0
     */
    Integer minTestThreshold = 0

    BuildBreakerOptions buildBreakerOptions = new BuildBreakerOptions()

    void buildBreaker(@DelegatesTo(BuildBreakerOptions) Closure closure) {
        closure.delegate = buildBreakerOptions
        closure()
    }

    /**
     * Options for build breaking
     */
    static class BuildBreakerOptions {

        /**
         * Milliseconds after which test execution will be terminated with a fail
         */
        Integer maxTestThreshold

        /**
         * List of test class name suffixes (e.g. LoanAmountVerificationTest)
         */
        List<String> testClassNameSuffixes = ['Test', 'Should', 'Spec']

        /**
         * A method to add additional suffixes
         */
        void addTestClassNameSuffix(String testClassNameSuffix) {
            testClassNameSuffixes << testClassNameSuffix
        }
    }
}
