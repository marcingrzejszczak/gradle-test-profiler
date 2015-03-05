package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin

import javax.inject.Inject

@CompileStatic
@Slf4j
class TestProfilerPlugin implements Plugin<Project> {

    public static final String PROFILE_TESTS_TASK_NAME = "profileTests"
    public static final String TIMEOUT_ADDER_TESTS_TASK_NAME = "addTimeout"

    @PackageScope static final String DEFAULT_TEST_TIMEOUT_PROPERTY = 'default.test.timeout'

    private static final String DEFAULT_REPORTS_FOLDER = '/reports/test_profiling'
    private static final String DEFAULT_SINGLE_REPORT_RELATIVE_PATH = "$DEFAULT_REPORTS_FOLDER/testsProfile.csv"
    private static final String DEFAULT_MERGED_REPORTS_RELATIVE_PATH = "$DEFAULT_REPORTS_FOLDER/summary.csv"
    private static final String COMPILE_TEST_GROOVY = 'compileTestGroovy'

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

    void apply(Project project) {
        TestProfilerPluginExtension extension = extensionCreator.createExtension(project)
        if(!extension.enabled) {
            return
        }
        setDefaults(project, extension)
        printExtensionValues(extension)
        modifyTestTasks(project, extension)
        createSummaryReportTask(project, extension)
        createAfterCompilationTestTaskModifier(project, extension)
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

    @CompileStatic(TypeCheckingMode.SKIP)
    private Task createAfterCompilationTestTaskModifier(Project project, TestProfilerPluginExtension extension) {
        AfterCompilationTestTaskModifier testTaskModifier = project.tasks.create(TIMEOUT_ADDER_TESTS_TASK_NAME, AfterCompilationTestTaskModifier)
        testTaskModifier.dependsOn(testCompilationTask(project))
        testTaskModifier.conventionMapping.with {
            testProfilerPluginExtension = { extension }
            outputDir = { project.sourceSets.test.output.classesDir }
        }
        return testTaskModifier
    }

    private Object testCompilationTask(Project project) {
        List testCompilationTasks = [JavaPlugin.COMPILE_TEST_JAVA_TASK_NAME]
        if (project.plugins.findPlugin('groovy')) {
            testCompilationTasks << COMPILE_TEST_GROOVY
        }
        return testCompilationTasks
    }

    private File mergedTestProfilingSummaryDir(TestProfilerPluginExtension extension) {
        File mergedTestProfilingSummaryDir = extension.mergedSummaryPath.parentFile
        mergedTestProfilingSummaryDir.mkdirs()
        return mergedTestProfilingSummaryDir
    }
}