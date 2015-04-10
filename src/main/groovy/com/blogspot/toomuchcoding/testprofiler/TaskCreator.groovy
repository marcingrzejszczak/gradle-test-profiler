package com.blogspot.toomuchcoding.testprofiler
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.Task

import static com.blogspot.toomuchcoding.testprofiler.TestProfilerPlugin.PROFILE_TESTS_TASK_NAME

@PackageScope
@Slf4j
class TaskCreator {

    Task buildReportMergerForProject(Project project, TestProfilerPluginExtension extension) {
        Task task = createTask(extension, project)
        if (!task) {
            return null
        }
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

    private ReportMergerTask createReportMerger(Project project, TestProfilerPluginExtension extension) {
        if (taskShouldntBeAdded(project)) {
            return null
        }
        ReportMergerTask reportMerger = project.tasks.create(PROFILE_TESTS_TASK_NAME, ReportMergerTask)
        project.gradle.afterProject { Project proj ->
            reportMerger.mustRunAfter(proj.getTasksByName('build', true))
        }
        log.info("Created a task [$PROFILE_TESTS_TASK_NAME] in root project")
        reportMerger.conventionMapping.with {
            testProfilerPluginExtension = { extension }
            mergedTestProfilingSummaryFile = { extension.mergedSummaryPath }
        }
        return reportMerger
    }

    private boolean taskShouldntBeAdded(Project project) {
        return project != project.rootProject
    }

    private NoOpTask createNoOpTask(Project project) {
        return project.tasks.create(PROFILE_TESTS_TASK_NAME, NoOpTask)
    }
}
