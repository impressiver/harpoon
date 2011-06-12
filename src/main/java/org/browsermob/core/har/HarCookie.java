package org.browsermob.core.har;

import com.google.code.morphia.annotations.Embedded;
import org.browsermob.core.json.ISO8601DateDeserializer;
import org.browsermob.core.json.ISO8601DateSerializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

@Embedded
public class HarCookie {
    private String name;
    private String value;
    private String path;
    private String domain;
    private Date expires;
    private Boolean httpOnly;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @JsonSerialize(using = ISO8601DateSerializer.class)
    public Date getExpires() {
        return expires;
    }

    @JsonDeserialize(using = ISO8601DateDeserializer.class)
    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public Boolean getHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(Boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
}
