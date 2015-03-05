package integration
import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult

class BasicFuncSpec extends IntegrationSpec {

    void setup() {
        fork = true //to make stdout assertion work with Gradle 2.x - http://forums.gradle.org/gradle/topics/unable-to-catch-stdout-stderr-when-using-tooling-api-i-gradle-2-x#reply_15357743
    }

    def "should create a summary file with report summary"() {
        given:
            copyResources("project_without_timeout", "")
        when:
            ExecutionResult result = runTasksSuccessfully('build', "profileTests")
        then:
            result.standardOutput.contains("Your tests report is ready")
        and:
            fileExists("build/reports/test_profiling/summary.csv")
            fileExists("build/reports/test_profiling/testsProfile.csv")
    }

    def 'should merge two reports for two separate modules'() {
        given:
            copyResources("multimodule_project_without_timeout", "")
        when:
            ExecutionResult result = runTasksSuccessfully('build', "profileTests")
        then:
            result.standardOutput.contains("Your tests report is ready")
        and:
            fileExists("module1/build/reports/test_profiling/testsProfile.csv")
            fileExists("module2/build/reports/test_profiling/testsProfile.csv")
        and:
            fileExists("build/reports/test_profiling/summary.csv")
            summaryReportContainsMergedValues(file('build/reports/test_profiling/summary.csv').text)
    }

    private void summaryReportContainsMergedValues(String reportText) {
        assert reportText.contains(':module1\tfoo.CalculatorTest\tshould_add_two_numbers')
        assert reportText.contains(':module1\tfoo.CalculatorTest\tshould_subtract_a_number_from_another')
        assert reportText.contains(':module2\tfoo.CalculatorTest\tshould_subtract_a_number_from_another')
        assert reportText.contains(':module2\tfoo.CalculatorTest\tshould_add_two_numbers')
    }
}
