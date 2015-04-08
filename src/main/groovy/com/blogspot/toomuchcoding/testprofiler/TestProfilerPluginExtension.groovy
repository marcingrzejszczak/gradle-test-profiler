package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.CompileStatic
import groovy.transform.ToString

import static com.blogspot.toomuchcoding.testprofiler.TestProfilerPluginExtension.BuildBreakerOptions.WhatToDo.*
import static com.blogspot.toomuchcoding.testprofiler.TestProfilerPluginExtension.BuildBreakerOptions.WhatToDo.Type.ACT
import static com.blogspot.toomuchcoding.testprofiler.TestProfilerPluginExtension.BuildBreakerOptions.WhatToDo.Type.BREAK_BUILD
import static com.blogspot.toomuchcoding.testprofiler.TestProfilerPluginExtension.BuildBreakerOptions.WhatToDo.Type.DISPLAY_WARNING

@CompileStatic
@ToString(includePackage = false, includeNames = true)
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
    @ToString(includePackage = false, includeNames = true)
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

        /**
         * List of regexps related to FQN of class that if matched will NOT break the test
         */
        List<String> testClassRegexpsToIgnore = []

        /**
         * A method to add regexps
         */
        void addTestClassRegexpToIgnore(String testClassRegexpToIgnore) {
            testClassRegexpsToIgnore << testClassRegexpToIgnore
        }

        IfTestsExceedMaxThreshold ifTestsExceedMaxThreshold = new IfTestsExceedMaxThreshold()

        void ifTestsExceedMaxThreshold(@DelegatesTo(IfTestsExceedMaxThreshold) Closure closure) {
            closure.delegate = ifTestsExceedMaxThreshold
            closure()
        }

        protected boolean shouldBreakBuild() {
            return ifTestsExceedMaxThreshold.whatToDo.type == BREAK_BUILD
        }

        protected boolean shouldDisplayWarning() {
            return ifTestsExceedMaxThreshold.whatToDo.type == DISPLAY_WARNING
        }

        protected boolean shouldAct() {
            return ifTestsExceedMaxThreshold.whatToDo.type == ACT
        }

        static class WhatToDo {
            public static final Closure DO_NOTHING = Closure.IDENTITY

            static enum Type {
                BREAK_BUILD, DISPLAY_WARNING, ACT
            }

            final Type type
            final Closure action

            WhatToDo(Type type, Closure action) {
                this.type = type
                this.action = action
            }

            boolean doNothing() {
                return action == DO_NOTHING
            }
        }


        static class IfTestsExceedMaxThreshold {
            /**
             * What type of action and what exactly should happen when build fails. Defaults to logging a warning
             */
            WhatToDo whatToDo = new WhatToDo(DISPLAY_WARNING, DO_NOTHING)

            /**
             * The build will be broken
             */
            void breakBuild() {
                whatToDo = new WhatToDo(BREAK_BUILD, DO_NOTHING)
            }

            /**
             * The warning will be displayed with default message
             */
            void displayWarning() {
                whatToDo = new WhatToDo(DISPLAY_WARNING, DO_NOTHING)
            }

            /**
             * The warning will be displayed basing on the result of closure
             */
            void displayWarning(Closure<String> closure) {
                whatToDo = new WhatToDo(DISPLAY_WARNING, closure)
            }

            /**
             * Custom action will take place
             */
            void act(Closure closure) {
                whatToDo = new WhatToDo(ACT, closure)
            }
        }

    }
}
