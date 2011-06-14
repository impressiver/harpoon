package com.webmetrics.harpoon.web;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Get;
import com.webmetrics.harpoon.data.TestResult;
import com.webmetrics.harpoon.data.TestResultDAO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@At("/")
public class Home {
    public static final String TEXT_HTML_CHARSET_UTF8 = "text/html; charset=utf8";
    private List<TestResult> testResults;

    private TestResultDAO testResultDAO;

    @Inject
    public Home(TestResultDAO testResultDAO) {
        this.testResultDAO = testResultDAO;
    }

    @Get
    public void get(HttpServletResponse response) {
        response.setContentType(TEXT_HTML_CHARSET_UTF8);
        testResults = testResultDAO.find().asList();
    }

    public List<TestResult> getTestResults() {
        return testResults;
    }
}
