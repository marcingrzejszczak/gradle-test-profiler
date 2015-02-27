package com.blogspot.toomuchcoding.testprofiler
import groovy.transform.PackageScope
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class TestProfilerPlugin implements Plugin<Project> {

    @PackageScope static final String TEST_PROFILER_TASK_NAME = "calculateTestExecutionTime"
    @PackageScope static final String SUMMARY_REPORT_TASK_NAME = "profileTests"

    void apply(Project project) {
        TestProfilerPluginExtension extension = project.extensions.create(TEST_PROFILER_TASK_NAME, TestProfilerPluginExtension)
        createSummaryReportTask(project, extension)
        createProfiledTestTask(project, extension)
    }

    private void createProfiledTestTask(Project project, TestProfilerPluginExtension extension) {
        Task task = project.tasks.create(TEST_PROFILER_TASK_NAME, ProfiledTest)
        task.group = 'Verification'
        task.description = "Prepares a report with execution time of your tests and performs custom logic upon profiling"
        task.conventionMapping.with {
            testProfilerPluginExtension = { extension }
            reportDir = { new File(project.buildDir, extension.reportOutputDir) }
            mergedTestProfilingSummaryDir = { new File(project.rootDir, extension.mergedSummaryDir) }
        }
    }

    private void createSummaryReportTask(Project project, TestProfilerPluginExtension extension) {
        Task task = project.tasks.create(SUMMARY_REPORT_TASK_NAME, ReportMerger)
        task.group = 'Verification'
        task.description = "Combines the reports into a single a file"
        task.dependsOn(TEST_PROFILER_TASK_NAME)
        task.conventionMapping.with {
            testProfilerPluginExtension = { extension }
            mergedTestProfilingSummaryDir = { new File(project.rootDir, extension.mergedSummaryDir) }
        }
    }
}