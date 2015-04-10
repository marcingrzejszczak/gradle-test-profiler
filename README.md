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
        classpath 'com.blogspot.toomuchcoding:gradle-test-profiler:0.2.2'
    }
}

apply plugin: 'com.blogspot.toomuchcoding.testprofiler'

```

### How to run it

It's enough to execute

```
./gradlew clean build profileTests

```

It's important run both *build* and *profileTests*

### How does it work

What this plugin does is:

  - adds TestExecutionListener that
    - prints out the time of execution of a test
    - creates a report for each module with sorted test execution data
  - if `maxThreshold` is passed:
    - manipulates the bytcode of tests by adding a JUnit Timeout Rule
    - for Spock adds GlobalExtension that times out the tests if needed
  - the `profileTests` task merges all the per-module sorted test execution data into one


### How to configure it

You have a special section called `testprofiler`

```
testprofiler {

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
         * Relative path to the report for a module. Defaults to {@code /reports/test_profiling/testsProfile.csv}
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

        /**
         * Additional options for build breaking
         */
        buildBreaker {

            /**
             * Milliseconds after which test execution will be terminated with a fail
             */
            Integer maxTestThreshold = 30_000

            /**
             * List of test class name suffixes (e.g. LoanAmountVerificationTest)
             */
            List<String> testClassNameSuffixes = ['Test', 'Should', 'Spec']

            /**
             * A method to add additional suffixes
             */
            addTestClassNameSuffix 'SomeOtherSuffix'
            
            /**
             * List of regexps related to FQN of class that if matched will NOT break the test
             */
            List<String> testClassRegexpsToIgnore = []
    
            /**
             * A method to add regexps of classes to ignore
             */
            void addTestClassRegexpToIgnore(String... testClassRegexpToIgnore) {
                testClassRegexpsToIgnore.addAll(testClassRegexpToIgnore)
            }

            /**
            * Section to describe what should happen if tests exceed max threshold
            */
            ifTestsExceedMaxThreshold {
                breakBuild()
            }
        }
}

```

### Actions if build exceeds max threshold

#### Display default warning message

If provided as follows then the build will display a default warning message if the test execution time exceeds the provided max one

```
testprofiler {

    buildBreaker {
                Integer maxTestThreshold = 30_000

                ifTestsExceedMaxThreshold {
                    displayWarning()
                }
            }
    }
}
```

#### Display custom warning message

If provided as follows then the build will display a custom warning message if the test execution time exceeds the provided max one

```
testprofiler {

    buildBreaker {
                Integer maxTestThreshold = 30_000

                ifTestsExceedMaxThreshold {
                    displayWarning { TestDescriptor testDescriptor, TestExecutionResult testExecutionResult ->
                        "return some string with [$testDescriptor] and [$testExecutionResult] that will be logged"
                    }
                }
            }
    }
}
```

#### Break the build

If provided as follows then the will break the build if the test takes too long too run.

```
testprofiler {

    buildBreaker {
                Integer maxTestThreshold = 30_000

                ifTestsExceedMaxThreshold {
                    breakBuild()
                }
            }
    }
}
```

#### Perform custom logic

If provided as follows then the custom logic will be executed if the test execution time exceeds the provided max one

```
testprofiler {

    buildBreaker {
                Integer maxTestThreshold = 30_000

                ifTestsExceedMaxThreshold {
                    act { com.blogspot.toomuchcoding.testprofiler.LoggerProxy,
                          TestDescriptor testDescriptor,
                          TestExecutionResult testExecutionResult ->
                        // do whatever you want to...
                    }
                }
            }
    }
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
