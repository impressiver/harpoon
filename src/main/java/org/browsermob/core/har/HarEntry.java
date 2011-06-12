package org.browsermob.core.har;

import com.google.code.morphia.annotations.Embedded;
import org.browsermob.core.json.ISO8601DateDeserializer;
import org.browsermob.core.json.ISO8601DateSerializer;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonAutoDetect
@Embedded
public class HarEntry {
    private String pageref;
    private Date startedDateTime;
    private long time;

    @Embedded
    private HarRequest request;

    @Embedded
    private HarResponse response;

    @Embedded
    private HarCache cache = new HarCache();

    @Embedded
    private HarTimings timings;

    private String serverIPAddress;

    public HarEntry() {
    }

    public HarEntry(String pageref) {
        this.pageref = pageref;
        this.startedDateTime = new Date();
    }

    public String getPageref() {
        return pageref;
    }

    public void setPageref(String pageref) {
        this.pageref = pageref;
    }

    @JsonSerialize(using = ISO8601DateSerializer.class)
    public Date getStartedDateTime() {
        return startedDateTime;
    }

    @JsonDeserialize(using = ISO8601DateDeserializer.class)
    public void setStartedDateTime(Date startedDateTime) {
        this.startedDateTime = startedDateTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public HarRequest getRequest() {
        return request;
    }

    public void setRequest(HarRequest request) {
        this.request = request;
    }

    public HarResponse getResponse() {
        return response;
    }

    public void setResponse(HarResponse response) {
        this.response = response;
    }

    public HarCache getCache() {
        return cache;
    }

    public void setCache(HarCache cache) {
        this.cache = cache;
    }

    public HarTimings getTimings() {
        return timings;
    }

    public void setTimings(HarTimings timings) {
        this.timings = timings;
    }

    public String getServerIPAddress() {
        return serverIPAddress;
    }

    public void setServerIPAddress(String serverIPAddress) {
        this.serverIPAddress = serverIPAddress;
    }
}
