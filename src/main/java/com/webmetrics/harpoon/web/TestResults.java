package com.webmetrics.harpoon.web;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Get;
import com.webmetrics.harpoon.data.TestResult;
import com.webmetrics.harpoon.data.TestResultDAO;

import java.util.List;

@At("/")
public class TestResults {
    private List<TestResult> testResults;

    private TestResultDAO testResultDAO;

    @Inject
    public TestResults(TestResultDAO testResultDAO) {
        this.testResultDAO = testResultDAO;
    }

    @Get
    public void get() {
        testResults = testResultDAO.find().asList();
    }

    public List<TestResult> getTestResults() {
        return testResults;
    }
}
