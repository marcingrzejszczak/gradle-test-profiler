package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test

import java.util.concurrent.ConcurrentHashMap

@PackageScope
@CompileStatic
@Slf4j
class TestTaskModifier {

    private final TestProfilerPluginExtension testProfilerPluginExtension
    private final Project project
    private final File mergedTestProfilingSummaryDir

    TestTaskModifier(File mergedTestProfilingSummaryDir,Project project, TestProfilerPluginExtension testProfilerPluginExtension) {
        this.mergedTestProfilingSummaryDir = mergedTestProfilingSummaryDir
        this.project = project
        this.testProfilerPluginExtension = testProfilerPluginExtension
    }

    void modifyCurrentTestTasks() {
        log.debug("Modifying test tasks for project [$project.name]")
        this.project.plugins.withType(JavaPlugin) {
            this.project.tasks.withType(Test) { Task task ->
                if (task.name == JavaPlugin.TEST_TASK_NAME) {
                    Set<TestExecutionResult> testExecutionResults = Collections.newSetFromMap(new ConcurrentHashMap<TestExecutionResult, Boolean>())
                    Test testTask = (Test) task
                    testTask.addTestListener(new TestExecutionResultSavingTestListener(testExecutionResults))
                    log.debug("Added test listener for task [$testTask.name]")
                    testTask.doLast {
                        log.debug("Stored results are $testExecutionResults")
                        new ReportStorerTask(testProfilerPluginExtension, project).storeReport(testExecutionResults)
                    }
                    log.debug("Added storing results as last action for task [$testTask.name]")
                }
            }
        }
    }



}
