package com.blogspot.toomuchcoding.testprofiler
import groovy.transform.PackageScope
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin

class TestProfilerPlugin implements Plugin<Project> {

    @PackageScope static final String TEST_PROFILER_EXTENSION = "calculateTestExecutionTime"
    @PackageScope static final String SUMMARY_REPORT_TASK_NAME = "profileTests"

    void apply(Project project) {
        TestProfilerPluginExtension extension = project.extensions.create(TEST_PROFILER_EXTENSION, TestProfilerPluginExtension)
        modifyTestTasks(project, extension)
        createSummaryReportTask(project, extension)
    }

    private void modifyTestTasks(Project project, TestProfilerPluginExtension extension) {
        File mergedTestProfilingSummaryDir = new File(project.rootDir, extension.mergedSummaryDir)
        File reportDir = new File(project.buildDir, extension.reportOutputDir)
        new TestTaskModifier(mergedTestProfilingSummaryDir, project, reportDir, extension).modifyCurrentTestTasks()
    }

    private void createSummaryReportTask(Project project, TestProfilerPluginExtension extension) {
        Task task = project.tasks.create(SUMMARY_REPORT_TASK_NAME, ReportMerger)
        task.group = 'Verification'
        task.description = "Combines the reports into a single a file"
        task.dependsOn(JavaPlugin.TEST_TASK_NAME)
        task.conventionMapping.with {
            testProfilerPluginExtension = { extension }
            mergedTestProfilingSummaryDir = { new File(project.rootDir, extension.mergedSummaryDir) }
        }
    }
}