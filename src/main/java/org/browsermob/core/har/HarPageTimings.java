package org.browsermob.core.har;

import com.google.code.morphia.annotations.Embedded;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;

@JsonWriteNullProperties(value=false)
@Embedded
public class HarPageTimings {
    private Long onContentLoad;
    private Long onLoad;

    public HarPageTimings() {
    }

    public HarPageTimings(Long onContentLoad, Long onLoad) {
        this.onContentLoad = onContentLoad;
        this.onLoad = onLoad;
    }

    public Long getOnContentLoad() {
        return onContentLoad;
    }

    public void setOnContentLoad(Long onContentLoad) {
        this.onContentLoad = onContentLoad;
    }

    public Long getOnLoad() {
        return onLoad;
    }

    public void setOnLoad(Long onLoad) {
        this.onLoad = onLoad;
    }
}
