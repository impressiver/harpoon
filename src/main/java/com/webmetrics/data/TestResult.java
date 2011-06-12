package com.webmetrics.data;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import org.browsermob.core.har.Har;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity
public class TestResult {
    @Id
    ObjectId id;

    private String apiKey;
    private String name;
    private String description;
    private boolean success;
    private Date created;

    @Embedded
    private Har har;

    @Embedded
    private TestResultError error;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Har getHar() {
        return har;
    }

    public void setHar(Har har) {
        this.har = har;
    }

    public TestResultError getError() {
        return error;
    }

    public void setError(TestResultError error) {
        this.error = error;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
