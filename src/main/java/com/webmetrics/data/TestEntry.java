package com.webmetrics.data;

import org.browsermob.core.har.HarEntry;

public class TestEntry {
    private String testName;

    HarEntry harEntry;
    private TestResult testResult;

    public TestEntry () {}

    public TestEntry (TestResult testResult, HarEntry harEntry) {
        this.testResult = testResult;
        this.testName = testResult.getName();
        this.harEntry = harEntry;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public HarEntry getHarEntry() {
        return harEntry;
    }

    public void setHarEntry(HarEntry harEntry) {
        this.harEntry = harEntry;
    }

    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }
}
