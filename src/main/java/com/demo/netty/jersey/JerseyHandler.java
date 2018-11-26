package com.demo.netty.jersey;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.WebApplication;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

@ChannelHandler.Sharable
public class JerseyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyHandler.class);

    private final transient WebApplication application;
    private final transient String baseUri;

    public JerseyHandler(final WebApplication application, final ResourceConfig resourceConfig) {
        super();
        this.application = application;
        this.baseUri = (String) resourceConfig.getProperty("/");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request)
            throws URISyntaxException, IOException {
        System.out.println("In channel read0");
        final String base = getBaseUri(request);
        final URI baseUri = new URI(base);
        final URI requestUri = new URI(base.substring(0, base.length() - 1) + request.uri());

        if (request.uri().equals("/favicon.ico"))
            return;

        final ContainerRequest cRequest = new ContainerRequest(application, request.method().name(), baseUri,
                requestUri, getHeaders(request), new ByteBufInputStream(request.content()));

        application.handleRequest(cRequest, new JerseyResponseWriter(ctx.channel()));
    }

    private InBoundHeaders getHeaders(final FullHttpRequest request) {
        final InBoundHeaders headers = new InBoundHeaders();
        HttpHeaders httpHeaders = request.headers();
        httpHeaders.forEach(e -> headers.put(e.getKey(), Arrays.asList(e.getValue())));
        return headers;
    }

    private String getBaseUri(final FullHttpRequest request) {
        String baseUri = this.baseUri;
        if (baseUri == null) {
            baseUri = "http://" + request.headers().get(HttpHeaderNames.HOST) + "/";
        }
        return baseUri;
    }
}