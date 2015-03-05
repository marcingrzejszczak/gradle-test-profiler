package com.blogspot.toomuchcoding.testprofiler
import groovy.transform.PackageScope
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin

import static com.blogspot.toomuchcoding.testprofiler.TestProfilerPlugin.PROFILE_TESTS_TASK_NAME

@PackageScope
class TaskCreator {

    Task buildTask(Project project, TestProfilerPluginExtension extension) {
        Task task = createTask(extension, project)
        task.group = 'Verification'
        task.description = "Creates a report of tests execution time"
        return task
    }

    private Task createTask(TestProfilerPluginExtension extension, Project project) {
        if (extension.enabled) {
            return createReportMerger(project, extension)
        } else {
            return createNoOpTask(project)
        }
    }

    private ReportMerger createReportMerger(Project project, TestProfilerPluginExtension extension) {
        ReportMerger reportMerger = project.tasks.create(PROFILE_TESTS_TASK_NAME, ReportMerger)
        reportMerger.dependsOn(TestProfilerPlugin.TIMEOUT_ADDER_TESTS_TASK_NAME)
        reportMerger.dependsOn(JavaPlugin.TEST_TASK_NAME)
        reportMerger.conventionMapping.with {
            testProfilerPluginExtension = { extension }
            if(extension.mergedSummaryPath?.exists()) { mergedTestProfilingSummaryFile = { extension.mergedSummaryPath } }
        }
        return reportMerger
    }

    private NoOpTask createNoOpTask(Project project) {
        return project.tasks.create(PROFILE_TESTS_TASK_NAME, NoOpTask)
    }
}
