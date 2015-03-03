package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin

@Slf4j
@CompileStatic
class TestProfilerPlugin implements Plugin<Project> {

    public static final String TEST_PROFILER_EXTENSION = "testprofiler"
    public static final String SUMMARY_REPORT_TASK_NAME = "profileTests"

    private static final String DEFAULT_REPORTS_FOLDER = '/reports/test_profiling'
    private static final String DEFAULT_SINGLE_REPORT_RELATIVE_PATH = "$DEFAULT_REPORTS_FOLDER/testsProfile.csv"
    private static final String DEFAULT_MERGED_REPORTS_RELATIVE_PATH = "$DEFAULT_REPORTS_FOLDER/summary.csv"

    @CompileStatic(TypeCheckingMode.SKIP)
    void apply(Project project) {
        TestProfilerPluginExtension extension = project.extensions.create(TEST_PROFILER_EXTENSION, TestProfilerPluginExtension)
        setDefaults(project, extension)
        printExtensionValues(extension)
        modifyTestTasks(project, extension)
        createSummaryReportTask(project, extension)
    }

    void printExtensionValues(TestProfilerPluginExtension testProfilerPluginExtension) {
        log.debug("Setting up profiling with the following parameters [$testProfilerPluginExtension]")
    }

    private void setDefaults(Project project, TestProfilerPluginExtension testProfilerPluginExtension) {
        if(!testProfilerPluginExtension.reportPath) {
            testProfilerPluginExtension.reportPath = new File(project.buildDir, DEFAULT_SINGLE_REPORT_RELATIVE_PATH)
        }
        if(!testProfilerPluginExtension.mergedSummaryPath) {
            testProfilerPluginExtension.mergedSummaryPath = new File(project.rootProject.buildDir, DEFAULT_MERGED_REPORTS_RELATIVE_PATH)
        }
    }

    private void modifyTestTasks(Project project, TestProfilerPluginExtension extension) {
        File mergedTestProfilingSummaryDir = mergedTestProfilingSummaryDir(extension)
        new TestTaskModifier(mergedTestProfilingSummaryDir, project, extension).modifyCurrentTestTasks()
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private void createSummaryReportTask(Project project, TestProfilerPluginExtension extension) {
        Task task = project.tasks.create(SUMMARY_REPORT_TASK_NAME, ReportMerger)
        task.group = 'Verification'
        task.description = "Combines the reports into a single a file"
        task.dependsOn(JavaPlugin.TEST_TASK_NAME)
        task.conventionMapping.with {
            testProfilerPluginExtension = { extension }
            mergedTestProfilingSummaryFile = { extension.mergedSummaryPath }
        }
    }

    private File mergedTestProfilingSummaryDir(TestProfilerPluginExtension extension) {
        File mergedTestProfilingSummaryDir = extension.mergedSummaryPath.parentFile
        mergedTestProfilingSummaryDir.mkdirs()
        return mergedTestProfilingSummaryDir
    }
}