package ads.netty.jersey;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.ContainerProvider;
import com.sun.jersey.spi.container.WebApplication;

public class JerseyContainerProvider implements ContainerProvider<SimpleHTTPHandler> {

    @Override
    public SimpleHTTPHandler createContainer(Class<SimpleHTTPHandler> type, ResourceConfig resourceConfig, WebApplication application) throws ContainerException {
        return type == SimpleHTTPHandler.class ? new SimpleHTTPHandler(application, resourceConfig) : null;
    }
}
