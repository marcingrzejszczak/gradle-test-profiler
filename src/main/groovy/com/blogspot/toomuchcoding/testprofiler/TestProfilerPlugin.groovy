package com.blogspot.toomuchcoding.testprofiler

import org.gradle.api.Plugin
import org.gradle.api.Project

class TestProfilerPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create("testprofiler", TestProfilerPluginExtension)
    }
}