package com.blogspot.toomuchcoding.testprofiler

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@CompileStatic
@Slf4j
class DefaultTestExecutionComparator implements Comparator<ReportRow> {

    protected static final Closure<Integer> DEFAULT_TEST_EXECUTION_COMPARATOR = { ReportRow o1, ReportRow o2 ->
        if (o1.testExecutionResult.testExecutionTime <=> o2.testExecutionResult.testExecutionTime != 0) {
            return o2.testExecutionResult.testExecutionTime <=> o1.testExecutionResult.testExecutionTime
        }
        if (o1.testExecutionResult.testClassName <=> o2.testExecutionResult.testClassName != 0) {
            return o2.testExecutionResult.testClassName <=> o1.testExecutionResult.testClassName
        }
        if (o1.testExecutionResult.testName <=> o2.testExecutionResult.testName != 0) {
            return o2.testExecutionResult.testName <=> o1.testExecutionResult.testName
        }
        if (o1.testClassExecutionTime <=> o2.testClassExecutionTime != 0) {
            return o2.testClassExecutionTime <=> o1.testClassExecutionTime
        }
        return o2.module <=> o1.module
    }

    @Override
    int compare(ReportRow o1, ReportRow o2) {
        return DEFAULT_TEST_EXECUTION_COMPARATOR(o1, o2)
    }
}
