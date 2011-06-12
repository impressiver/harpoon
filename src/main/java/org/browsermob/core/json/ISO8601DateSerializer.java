package org.browsermob.core.json;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.ScalarSerializerBase;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Date;

public class ISO8601DateSerializer extends ScalarSerializerBase<Date> {
    public final static ISO8601DateSerializer INSTANCE = new ISO8601DateSerializer();

    public ISO8601DateSerializer() {
        super(Date.class);
    }

    @Override
    public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        DateFormat df = (DateFormat) provider.getConfig().getDateFormat().clone();
        jgen.writeString(df.format(value));
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        return createSchemaNode("string", true);
    }

}
