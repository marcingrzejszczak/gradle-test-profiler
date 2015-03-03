package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.PackageScope
import org.gradle.api.Project

@PackageScope
class ExtensionCreator {

    protected static final String TEST_PROFILER_EXTENSION = "testprofiler"

    TestProfilerPluginExtension createExtension(Project project) {
        return project.extensions.create(TEST_PROFILER_EXTENSION, TestProfilerPluginExtension)
    }
}
