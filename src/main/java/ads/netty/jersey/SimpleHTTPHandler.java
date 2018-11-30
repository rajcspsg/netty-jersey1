package ads.netty.jersey;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.WebApplication;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.util.Arrays;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
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
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer("My Netty".getBytes()), false);
        response.headers().add(request.headers());
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        boolean isKeepAlive = isKeepAlive(request);
        if (isKeepAlive) {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ChannelFuture f = ctx.writeAndFlush(response);
        if(!isKeepAlive)
            f.addListener(ChannelFutureListener.CLOSE);
        ctx.close();
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
        String baseUri = this.getBaseUri(request);
        if (baseUri == null) {
            baseUri = "http://" + request.headers().get(HttpHeaderNames.HOST) + "/";
        }
        return baseUri;
    }
}
