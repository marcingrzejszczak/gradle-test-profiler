[![Build Status](https://travis-ci.org/marcingrzejszczak/gradle-test-profiler.svg)](https://travis-ci.org/marcingrzejszczak/gradle-test-profiler)
[ ![Download](https://api.bintray.com/packages/marcingrzejszczak/com-blogspot-toomuchcoding/gradle-test-profiler/images/download.svg) ](https://bintray.com/marcingrzejszczak/com-blogspot-toomuchcoding/gradle-test-profiler/_latestVersion)
[![Coverage Status](https://coveralls.io/repos/marcingrzejszczak/gradle-test-profiler/badge.svg)](https://coveralls.io/r/marcingrzejszczak/gradle-test-profiler)

# gradle-test-profiler

Created with @AChudzik

The idea of this plugin is to perform profiling of your tests. You will be able to see your test
execution times sorted in the descending manner together with an information about a module
and the class name from which the test was executed.

## Since version 0.1.0

### How to add it

You have to add `jcenter` to buildscript repositories

```

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.blogspot.toomuchcoding:gradle-test-profiler:0.1.0'
    }
}

apply plugin: 'com.blogspot.toomuchcoding.testprofiler'

```


### How to run it

It's enough to execute

```
./gradlew clean profileTests

```


### How to configure it

You have a special section called `testprofiler`

```
testprofiler {
    
    // Separator of columns in the output report
    separator = '\t'
    
    // Headers in the report
    outputReportHeaders = "module${separator}test class name${separator}test name${separator}test execution time in [s]${separator}test class execution time in [s]\n"

    // Closure that will be converted to a Comparator to compare row entries
    comparator = DefaultTestExecutionComparator.DEFAULT_TEST_EXECUTION_COMPARATOR

    // Closure that converts a reporter row entry to a single String
    rowFromReport = ReportStorer.DEFAULT_ROW_FROM_REPORT_CONVERTER

    // Base directory where reports will be gathered. The parent of this directory is build dir of the project
    reportOutputDir = "reports/test_profiling"

    // Filename of a single report
    reportOutputCsvFilename = "testsProfile.csv"

    // Base directory where merged summary of reports will be kept. The parent of directory is the top root project dir
    mergedSummaryDir = "reports/test_profiling"

    // Filename of a merged summary of reports
    mergedSummaryFileName = "summary.csv"
}

```


## Deprecated (up till version 0.0.4)

### How to add it

For the time being just enter in your project

```
if (project.hasProperty('testsProfiling')) {
    apply from: 'https://raw.githubusercontent.com/marcingrzejszczak/gradle-test-profiler/0.0.4/test_profiling.gradle'
}
```

### How to run it?

Execute

```
./gradlew clean build testsProfileSummaryReport -PtestsProfiling
```
