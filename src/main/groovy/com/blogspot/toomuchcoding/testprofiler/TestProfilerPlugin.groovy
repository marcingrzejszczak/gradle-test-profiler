package com.blogspot.toomuchcoding.testprofiler
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Plugin
import org.gradle.api.Project

import javax.inject.Inject

@CompileStatic
class TestProfilerPlugin implements Plugin<Project> {

    public static final String PROFILE_TESTS_TASK_NAME = "profileTests"

    private static final String DEFAULT_REPORTS_FOLDER = '/reports/test_profiling'
    private static final String DEFAULT_SINGLE_REPORT_RELATIVE_PATH = "$DEFAULT_REPORTS_FOLDER/testsProfile.csv"
    private static final String DEFAULT_MERGED_REPORTS_RELATIVE_PATH = "$DEFAULT_REPORTS_FOLDER/summary.csv"

    private TestTaskModifier testTaskModifier
    private final LoggerProxy loggerProxy
    private final ExtensionCreator extensionCreator

    @Inject
    TestProfilerPlugin() {
        this.loggerProxy = new LoggerProxy()
        this.extensionCreator = new ExtensionCreator()
    }

    protected TestProfilerPlugin(TestTaskModifier testTaskModifier, LoggerProxy loggerProxy, ExtensionCreator extensionCreator) {
        this.testTaskModifier = testTaskModifier
        this.loggerProxy = loggerProxy
        this.extensionCreator = extensionCreator
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    void apply(Project project) {
        TestProfilerPluginExtension extension = extensionCreator.createExtension(project)
        if(!extension.enabled) {
            return
        }
        setDefaults(project, extension)
        printExtensionValues(extension)
        modifyTestTasks(project, extension)
        createSummaryReportTask(project, extension)
    }

    private void printExtensionValues(TestProfilerPluginExtension testProfilerPluginExtension) {
        loggerProxy.debug("Setting up profiling with the following parameters [$testProfilerPluginExtension]")
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

    private void createSummaryReportTask(Project project, TestProfilerPluginExtension extension) {
        new TaskCreator().buildTask(project, extension)
    }

    private File mergedTestProfilingSummaryDir(TestProfilerPluginExtension extension) {
        File mergedTestProfilingSummaryDir = extension.mergedSummaryPath.parentFile
        mergedTestProfilingSummaryDir.mkdirs()
        return mergedTestProfilingSummaryDir
    }
}