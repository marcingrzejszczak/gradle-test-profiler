package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.CompileStatic
import groovy.transform.Immutable

/**
 * @author Adam Chudzik
 * @author Marcin Grzejszczak
 */
@CompileStatic
@Immutable
class TestExecutionResult {
    String testClassName
    String testName
    Double testExecutionTime
}
