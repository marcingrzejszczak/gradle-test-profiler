package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.PackageScope
import groovy.transform.CompileStatic
import org.gradle.api.tasks.testing.Test

@PackageScope
@CompileStatic
class TestTaskBasedGroovyClassLoader extends GroovyClassLoader {

    TestTaskBasedGroovyClassLoader(Test test) {
        super(test.class.classLoader)
        addURL(test.testClassesDir.toURI().toURL())
        test.classpath.files.each {
            addURL(it.toURI().toURL())
        }
    }
}
