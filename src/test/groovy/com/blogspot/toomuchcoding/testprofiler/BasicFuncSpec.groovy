package com.blogspot.toomuchcoding.testprofiler

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult

class BasicFuncSpec extends IntegrationSpec {

    void setup() {
        fork = true //to make stdout assertion work with Gradle 2.x - http://forums.gradle.org/gradle/topics/unable-to-catch-stdout-stderr-when-using-tooling-api-i-gradle-2-x#reply_15357743
    }

    def "should create a summary file with report summary"() {
        given:
            copyResources("sample_project", "")
        when:
            ExecutionResult result = runTasksSuccessfully("profileTests")
        then:
            result.standardOutput.contains("Your tests report is ready")
        and:
            fileExists("build/reports/test_profiling/summary.csv")
            fileExists("build/reports/test_profiling/testsProfile.csv")
    }

    def 'should merge two reports for two separate modules'() {

    }
}
