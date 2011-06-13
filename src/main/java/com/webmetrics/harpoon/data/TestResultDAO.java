package com.webmetrics.harpoon.data;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.QueryResults;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.Mongo;
import org.bson.types.ObjectId;

import java.util.List;

@Singleton
public class TestResultDAO extends BasicDAO<TestResult, ObjectId> {
    @Inject
    public TestResultDAO(Mongo mongo, Morphia morphia) {
        super(mongo, morphia, "harpoon");
    }

    @Override
    public QueryResults<TestResult> find() {
        return ds.find(entityClazz).order("-created");
    }

    public List<TestResult> findByTestName(String testName, int limit) {
        return ds.find(entityClazz).filter("name", testName).order("created").limit(limit).asList();
    }

    public List<TestResult> findByRequestUrl(String uri, int limit) {
        return ds.find(entityClazz).filter("har.log.entries.request.url", uri).order("-created").limit(limit).asList();
    }

    public List<TestResult> findByRequestUrl(String uri, String testName, int limit) {
        return ds.find(entityClazz).filter("name", testName).filter("har.log.entries.request.url", uri).order("created").limit(limit).asList();
    }
}
