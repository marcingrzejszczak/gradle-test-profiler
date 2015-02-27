package com.blogspot.toomuchcoding.testprofiler
import groovy.transform.PackageScope
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class TestProfilerPlugin implements Plugin<Project> {

    @PackageScope static final String TEST_PROFILER_TASK_NAME = "testprofiler"

    void apply(Project project) {
        TestProfilerPluginExtension extension = project.extensions.create(TEST_PROFILER_TASK_NAME, TestProfilerPluginExtension)
        createProfiledTestTask(project, extension)
    }

    private void createProfiledTestTask(Project project, TestProfilerPluginExtension extension) {
        Task task = project.tasks.create(TEST_PROFILER_TASK_NAME, ProfiledTest)
        task.group = 'Verification'
        task.description = "Prepares a report with execution time of your tests and performs custom logic upon profiling"
        task.conventionMapping.with {
            testProfilerPluginExtension = { extension }
        }
    }
}