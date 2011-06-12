package org.browsermob.core.har;

import com.google.code.morphia.annotations.Embedded;
import org.browsermob.core.json.ISO8601DateDeserializer;
import org.browsermob.core.json.ISO8601DateSerializer;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

@JsonWriteNullProperties(value=false)
@Embedded
public class HarCacheStatus {
    private Date expires;
    private Date lastAccess;
    private String eTag;
    private int hitCount;

    @JsonSerialize(using = ISO8601DateSerializer.class)
    public Date getExpires() {
        return expires;
    }

    @JsonDeserialize(using = ISO8601DateDeserializer.class)
    public void setExpires(Date expires) {
        this.expires = expires;
    }

    @JsonSerialize(using = ISO8601DateSerializer.class)
    public Date getLastAccess() {
        return lastAccess;
    }

    @JsonDeserialize(using = ISO8601DateDeserializer.class)
    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }
}
