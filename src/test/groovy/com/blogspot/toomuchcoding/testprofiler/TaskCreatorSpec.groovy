package com.blogspot.toomuchcoding.testprofiler

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class TaskCreatorSpec extends Specification {

    TaskCreator taskCreator = new TaskCreator()
    Project project = ProjectBuilder.builder().build()

    def 'should build NoOpTask when plugin is disabled'() {
        when:
            Task task = taskCreator.buildReportMergerForProject(project, new TestProfilerPluginExtension(enabled: false))
        then:
            task instanceof NoOpTask
    }

    def 'should build ReportMerger when plugin is enabled'() {
        when:
            Task task = taskCreator.buildReportMergerForProject(project, new TestProfilerPluginExtension())
        then:
            task instanceof ReportMergerTask
    }
}
