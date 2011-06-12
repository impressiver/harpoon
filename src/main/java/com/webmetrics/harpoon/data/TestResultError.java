package com.webmetrics.harpoon.data;

import com.google.code.morphia.annotations.Embedded;

@Embedded
public class TestResultError {
    private int code;
    private String message;
    private String file;
    private int line;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
