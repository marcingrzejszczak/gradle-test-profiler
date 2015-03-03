package com.blogspot.toomuchcoding.testprofiler

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class TestProfilerPluginSpec extends Specification {

    Project project = ProjectBuilder.builder().build()
    TestTaskModifier testTaskModifier = Mock()
    LoggerProxy loggerProxy = Mock()
    ExtensionCreator extensionCreator = Stub()

    def "should not do anything in the configuration phase if test profiler plugin is disabled"() {
        given:
            TestProfilerPlugin testProfilerPlugin = new TestProfilerPlugin(testTaskModifier, loggerProxy, extensionCreator)
            extensionCreator.createExtension(project) >> new TestProfilerPluginExtension(enabled: false)
        when:
            testProfilerPlugin.apply(project)
        then:
            0 * testTaskModifier._

    }

}
