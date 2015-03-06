package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.transform.TypeCheckingMode
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin

@PackageScope
@CompileStatic
class BuildBreaker {

    private static final String COMPILE_TEST_GROOVY = 'compileTestGroovy'

    private final Project project
    private final TestProfilerPluginExtension pluginExtension
    private final LoggerProxy loggerProxy

    BuildBreaker(Project project, TestProfilerPluginExtension pluginExtension, LoggerProxy loggerProxy) {
        this.project = project
        this.pluginExtension = pluginExtension
        this.loggerProxy = loggerProxy
    }

    void performBuildBreakingLogic() {
        createAfterCompilationTestTaskModifier()
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private Task createAfterCompilationTestTaskModifier() {
        AfterCompilationTestTaskModifier testTaskModifier = project.tasks.create(TestProfilerPlugin.TIMEOUT_ADDER_TESTS_TASK_NAME, AfterCompilationTestTaskModifier)
        testTaskModifier.dependsOn(testCompilationTask(project))
        project.tasks.getByName(JavaPlugin.TEST_TASK_NAME).dependsOn(testTaskModifier)
        testTaskModifier.conventionMapping.with {
            testProfilerPluginExtension = { pluginExtension }
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
}
