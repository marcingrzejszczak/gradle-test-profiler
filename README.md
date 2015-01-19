# gradle-test-profiler

## How to add it

For the time being just enter in your project

```
if (project.hasProperty('testsProfiling')) {
    apply from: 'https://raw.githubusercontent.com/marcingrzejszczak/gradle-test-profiler/0.0.2/test_profiling.gradle'
}
```

## How to run it?

Execute

```
./gradlew clean build sortSummary -PtestsProfiling
```
