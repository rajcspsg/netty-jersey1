import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.ContainerProvider;
import com.sun.jersey.spi.container.WebApplication;
import me.netty.jersey.JerseyHandler;

public class NettyContainerProvider implements ContainerProvider<JerseyHandler> {

    @Override
    public JerseyHandler createContainer(Class<JerseyHandler> type, ResourceConfig resourceConfig,
                                         WebApplication application) throws ContainerException {
        return type == JerseyHandler.class ? new JerseyHandler(application, resourceConfig) : null;
    }
}

