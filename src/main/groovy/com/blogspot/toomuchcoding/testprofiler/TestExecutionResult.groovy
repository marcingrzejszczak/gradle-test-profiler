package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * @author Adam Chudzik
 * @author Marcin Grzejszczak
 */
@CompileStatic
@Immutable
@ToString(includePackage = false, includeNames = true)
class TestExecutionResult {
    String testClassName
    String testName
    Double testExecutionTime
}
