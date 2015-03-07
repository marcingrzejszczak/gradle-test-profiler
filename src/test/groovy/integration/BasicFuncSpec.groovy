package integration
import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult
import org.gradle.api.logging.LogLevel

class BasicFuncSpec extends IntegrationSpec {

    void setup() {
        fork = true //to make stdout assertion work with Gradle 2.x - http://forums.gradle.org/gradle/topics/unable-to-catch-stdout-stderr-when-using-tooling-api-i-gradle-2-x#reply_15357743
        logLevel = LogLevel.DEBUG
    }

    def "should not do anything if plugin is disabled"() {
        given:
            copyResources("project_with_disabled_plugin", "")
        when:
            ExecutionResult result = runTasksSuccessfully('build', "profileTests")
        then:
            !taskHasBeenExecuted(result)
        and:
            !reportsHaveBeenCreated()
    }

    def "should create a report and summary for a module that has plugin enabled"() {
        given:
            copyResources("multimodule_project_with_disabled_plugin", "")
        when:
            ExecutionResult result = runTasksSuccessfully('build', "profileTests")
        then:
            taskHasBeenExecuted(result)
        and:
            !reportExistsForModule('module1')
            reportExistsForModule('module2')
        and:
            reportSummaryHasBeenCreated()
            reportSummaryHasNoStatsFromModule1()

    }

    def "should create a summary file with report summary"() {
        given:
            copyResources("project_without_timeout", "")
        when:
            ExecutionResult result = runTasksSuccessfully('build', "profileTests")
        then:
            taskHasBeenExecuted(result)
        and:
            reportsHaveBeenCreated()
    }

    def 'should merge two reports for two separate modules'() {
        given:
            copyResources("multimodule_project_without_timeout", "")
        when:
            ExecutionResult result = runTasksSuccessfully('build', "profileTests")
        then:
            taskHasBeenExecuted(result)
        and:
            reportExistsForModule('module1')
            reportExistsForModule('module2')
        and:
            reportSummaryHasBeenCreated()
            summaryReportContainsMergedValues(file('build/reports/test_profiling/summary.csv').text)
    }

    private boolean reportSummaryHasBeenCreated() {
        return fileExists("build/reports/test_profiling/summary.csv")
    }

    private boolean reportExistsForModule(String module) {
        return fileExists("$module/build/reports/test_profiling/testsProfile.csv")
    }

    private boolean reportsHaveBeenCreated() {
        return fileExists("build/reports/test_profiling/summary.csv") &&
                fileExists("build/reports/test_profiling/testsProfile.csv")
    }


    private void reportSummaryHasNoStatsFromModule1() {
        assert !file('build/reports/test_profiling/summary.csv').text.contains('module1')
    }

    private boolean taskHasBeenExecuted(ExecutionResult result) {
        return result.standardOutput.contains("Your tests report is ready")
    }

    private void summaryReportContainsMergedValues(String reportText) {
        summaryReportContainsValuesFromModule('module1', reportText)
        summaryReportContainsValuesFromModule('module2', reportText)
    }

    private void summaryReportContainsValuesFromModule(String moduleName, String reportText) {
        assert reportText.contains(":$moduleName\tfoo.CalculatorTest\tshould_add_two_numbers")
        assert reportText.contains(":$moduleName\tfoo.CalculatorTest\tshould_subtract_a_number_from_another")
    }
}
