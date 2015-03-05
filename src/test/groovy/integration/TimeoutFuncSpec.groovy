package integration

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult
import spock.lang.Unroll

class TimeoutFuncSpec extends IntegrationSpec {

    void setup() {
        fork = true //to make stdout assertion work with Gradle 2.x - http://forums.gradle.org/gradle/topics/unable-to-catch-stdout-stderr-when-using-tooling-api-i-gradle-2-x#reply_15357743
    }

    @Unroll
    def "should fail the tests in project [#projectName] due to timeout"() {
        given:
            copyResources(projectName, "")
        when:
            ExecutionResult result = runTasksWithFailure("build", "profileTests")
        then:
            String stdout = result.standardOutput.toString()
            assertThatTestFailed(stdout, 'foo.CalculatorTest')
            assertThatTestFailed(stdout, 'foo.CalculatorSpec')
            assertThatStandardOuputContains(stdout, 'test timed out after 1 milliseconds')
            assertThatStandardOuputContains(stdout, 'Method timed out after 0.00 seconds')
        where:
            projectName << ['project_with_timeout', 'project_with_timeout_with_spock_1']
    }

    void assertThatTestFailed(String standardOutput, String className) {
        assert standardOutput.contains("$className > should_add_two_numbers FAILED")
    }

    void assertThatStandardOuputContains(String standardOutput, String text) {
        assert standardOutput.contains(text)
    }

    def "should fail the tests due to timeout even though there already is a timeout set"() {
        given:
            copyResources("project_with_multiple_timeouts", "")
        when:
            ExecutionResult result = runTasksWithFailure("build", "profileTests")
        then:
            result.standardOutput.toString().contains("java.lang.Exception: test timed out after 1 milliseconds")
    }

}
