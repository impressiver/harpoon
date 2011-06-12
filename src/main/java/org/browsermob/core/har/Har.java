package org.browsermob.core.har;

import com.google.code.morphia.annotations.Embedded;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

@Embedded
public class Har {
    @Embedded
    private HarLog log;

    public Har() {
    }

    public Har(HarLog log) {
        this.log = log;
    }

    public HarLog getLog() {
        return log;
    }

    public void setLog(HarLog log) {
        this.log = log;
    }

    public void writeTo(Writer writer) throws IOException {
        ObjectMapper om = new ObjectMapper();
        om.writeValue(writer, this);
    }

    public void writeTo(OutputStream os) throws IOException {
        ObjectMapper om = new ObjectMapper();
        om.writeValue(os, this);
    }

    public void writeTo(File file) throws IOException {
        ObjectMapper om = new ObjectMapper();
        om.writeValue(file, this);
    }
}
