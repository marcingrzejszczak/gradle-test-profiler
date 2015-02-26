package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.CompileStatic
import groovy.transform.Immutable

/**
 * @author Adam Chudzik
 * @author Marcin Grzejszczak
 */
@CompileStatic
@Immutable(knownImmutableClasses = [TestExecutionResult])
class ReportRow {
    String module
    TestExecutionResult testExecutionResult
    Double testClassExecutionTime
}
