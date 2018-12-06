package ads.netty.jersey.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;


@Provider
public class MyObjectMapperProvider implements ContextResolver<ObjectMapper> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        MAPPER.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
        //MAPPER.disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        System.out.println("MyJacksonProvider.getContext() called with type: "+type);
        return MAPPER;
    }
}
