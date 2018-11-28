import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.core.ResourceConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import me.netty.jersey.JerseyHandler;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final int MESSAGE_BYTES_LIMIT = 128 * 1024;

    private final ChannelHandler jerseyHandler;

    public HttpServerInitializer(final ResourceConfig resourceConfig) {
        jerseyHandler = createJerseyHandler(resourceConfig);
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();

        p.addLast("encoder", new HttpResponseEncoder());
        p.addLast("decoder", new HttpRequestDecoder());
        p.addLast("aggregator", new HttpObjectAggregator(MESSAGE_BYTES_LIMIT));
        p.addLast("jerseyHandler", jerseyHandler);
    }

    private ChannelHandler createJerseyHandler(final ResourceConfig resourceConfig) {
        return ContainerFactory.createContainer(JerseyHandler.class, resourceConfig);
    }
}
