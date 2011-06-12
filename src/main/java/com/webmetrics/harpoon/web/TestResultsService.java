package com.webmetrics.harpoon.web;

import com.google.code.morphia.Key;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.sitebricks.At;
import com.google.sitebricks.client.transport.Json;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;
import com.webmetrics.harpoon.data.TestResult;
import com.webmetrics.harpoon.data.TestResultDAO;
import org.bson.types.ObjectId;

import javax.servlet.http.HttpServletRequest;

@At("/test-results")
@Service
public class TestResultsService {
    private TestResultDAO testResultDAO;

    @Inject
    TestResultsService(TestResultDAO testResultDAO) {
        this.testResultDAO = testResultDAO;
    }

    @Get
    @At("/:id")
    public Reply<?> getTestResult(@Named("id") String id) {
        if (null == id) {
            System.out.println("No ID specified!");
            return Reply.saying().notFound();
        }

        TestResult result = testResultDAO.findOne("id", new ObjectId(id));

        return Reply.with(result).as(Json.class);
    }

    @Post
    public Reply<String> postTestResult(HttpServletRequest servletRequest, Request request) {
        TestResult result = request.read(TestResult.class).as(Json.class);

        Key<TestResult> key = testResultDAO.save(result);

        return Reply.with(key.getId().toString()).as(Json.class);
    }
}
