package ads.netty.jersey;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.WebApplication;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import java.net.URI;
import java.util.Arrays;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class SimpleHTTPHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final WebApplication applicationHandler;
    private final ResourceConfig resourceConfig;

    public WebApplication getApplicationHandler() {
        return applicationHandler;
    }

    public ResourceConfig getResourceConfig() {
        return resourceConfig;
    }

    public SimpleHTTPHandler(WebApplication applicationHandler, ResourceConfig resourceConfig) {
        super(false);
        this.applicationHandler = applicationHandler;
        this.resourceConfig = resourceConfig;
    }

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        System.out.println("in SimpleHTTPHandler");
        try {
            if (request.uri().equals("/favicon.ico")) return;
            final String base = getBaseUri(request);
            final URI baseUri = new URI(base);
            URI fullRequestUri = new URI(base + request.uri().substring(1));
            System.out.println("baseUri " + baseUri + " fullRequestUri " + fullRequestUri);
            final ContainerRequest cRequest = new ContainerRequest(applicationHandler, request.method().name(), baseUri,
                    fullRequestUri, getHeaders(request), new ByteBufInputStream(request.content()));
            applicationHandler.handleRequest(cRequest, new JerseyResponseWriter(ctx));

        } catch (Exception e) {
            ctx.writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(e.toString().getBytes())));
            e.printStackTrace();
        } finally {
            if(!HttpUtil.isKeepAlive(request)) ctx.close();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().read();
    }

    private InBoundHeaders getHeaders(final FullHttpRequest request) {
        final InBoundHeaders headers = new InBoundHeaders();
        HttpHeaders httpHeaders = request.headers();
        httpHeaders.forEach(e -> headers.put(e.getKey(), Arrays.asList(e.getValue())));
        return headers;
    }

    private String getBaseUri(final FullHttpRequest request) {
        return "http://" + request.headers().get(HttpHeaderNames.HOST) + "/";
    }
}
