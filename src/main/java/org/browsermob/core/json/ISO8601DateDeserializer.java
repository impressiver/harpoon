package org.browsermob.core.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.StdScalarDeserializer;
import org.codehaus.jackson.map.util.StdDateFormat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class ISO8601DateDeserializer extends StdScalarDeserializer<Date> {
    private DateFormat dateFormat = StdDateFormat.getBlueprintISO8601Format(); 
    
    protected ISO8601DateDeserializer() {
        super(Date.class);
    }

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        try {
            return dateFormat.parse(jp.getText());
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }
}
