package org.browsermob.core.har;

import com.google.code.morphia.annotations.Embedded;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Embedded
public class HarCache {
    @Embedded
    private HarCacheStatus beforeRequest;

    @Embedded
    private HarCacheStatus afterRequest;

    public HarCacheStatus getBeforeRequest() {
        return beforeRequest;
    }

    public void setBeforeRequest(HarCacheStatus beforeRequest) {
        this.beforeRequest = beforeRequest;
    }

    public HarCacheStatus getAfterRequest() {
        return afterRequest;
    }

    public void setAfterRequest(HarCacheStatus afterRequest) {
        this.afterRequest = afterRequest;
    }
}
