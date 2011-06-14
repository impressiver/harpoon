package com.webmetrics.harpoon.web;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.http.Get;
import com.webmetrics.harpoon.data.TestResult;
import com.webmetrics.harpoon.data.TestResultDAO;
import org.apache.commons.lang.CharEncoding;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@At("/:name")
public class TestResults {
    private List<TestResult> testResults;

    private TestResultDAO testResultDAO;

    @Inject
    public TestResults(TestResultDAO testResultDAO) {
        this.testResultDAO = testResultDAO;
    }

    @Get
    public void get(@Named("name") String name, HttpServletResponse response) {
        response.setContentType(Home.TEXT_HTML_CHARSET_UTF8);
        
        if (null == name) {
            System.out.println("No test name specified!");
            return;
        }

        String decodedName;
        try {
            decodedName = URLDecoder.decode(name, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            Reply.saying().error();
            return;
        }

        testResults = testResultDAO.findByTestName(decodedName, 100);
    }

    public List<TestResult> getTestResults() {
        return testResults;
    }
}
