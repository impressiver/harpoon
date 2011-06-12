package com.webmetrics.web;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.sitebricks.At;
import com.google.sitebricks.client.transport.Json;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;
import com.webmetrics.data.TestEntry;
import com.webmetrics.data.TestResult;
import com.webmetrics.data.TestResultDAO;
import org.apache.commons.lang.CharEncoding;
import org.browsermob.core.har.HarEntry;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@At("/request-history")
@Service
public class RequestHistoryService {
    private TestResultDAO testResultDAO;

    @Inject
    RequestHistoryService(TestResultDAO testResultDAO) {
        this.testResultDAO = testResultDAO;
    }

    @Get
    @At("/:name/:uri")
    public Reply<?> getTestResult(@Named("name") String name, @Named("uri") String uri) {
        if (null == uri) {
            System.out.println("No URI specified!");
            return Reply.saying().notFound();
        }

        String decodedName;
        String decodedUri;
        try {
            decodedName = URLDecoder.decode(name, CharEncoding.UTF_8);
            decodedUri = URLDecoder.decode(uri, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return Reply.saying().status(500);
        }

        List<TestResult> results;
        results = testResultDAO.findByRequestUrl(decodedUri, decodedName, 100);

        ArrayList<TestEntry> testEntries = new ArrayList<TestEntry>();
        for (TestResult testResult : results) {
            for (HarEntry entry : testResult.getHar().getLog().getEntries()) {
                if (decodedUri.equals(entry.getRequest().getUrl())) {
                    testEntries.add(new TestEntry(testResult, entry));
                }
            }
        }

        return Reply.with(testEntries).as(Json.class);
    }
}
