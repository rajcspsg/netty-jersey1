package me.netty.jersey;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.spi.container.WebApplication;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import javax.ws.rs.core.SecurityContext;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Arrays;

@ChannelHandler.Sharable
public class JerseyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final transient WebApplication application;
    private final transient String baseUri;

    public JerseyHandler(WebApplication applicationHandler, final ResourceConfig resourceConfig) {

        this.application = applicationHandler;
        //baseUri = resourceConfig.getProperty("/hellonetty").toString();
        baseUri = null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, FullHttpRequest request) throws Exception {
        System.out.println("request received \n "+ request);
        URI applicationURI = createAppURI(
                request.getProtocolVersion().protocolName().toLowerCase(),
                context.channel().localAddress().toString()
        );

        URI fullRequestUri = createFullURI(
                request.getProtocolVersion().protocolName().toLowerCase(),
                context.channel().localAddress().toString(),
                request.getUri()
        );

        final String base = getBaseUri(request);
        final URI baseUri = new URI(base);
        final URI requestUri = new URI(base.substring(0, base.length() - 1)
                + request.getUri());

        final ContainerRequest cRequest = new ContainerRequest(application, request.method().name(), baseUri,
                fullRequestUri, getHeaders(request), new ByteBufInputStream(request.content()));

        application.handleRequest(cRequest, new JerseyResponseWriter(context.channel()));
    }

    private URI createAppURI(String protocolName, String address) throws URISyntaxException {
        return new URI(String.format("%s:/%s", protocolName, address));
    }

    private URI createFullURI(String protocolName, String address, String path) throws URISyntaxException {
        return new URI(String.format("%s:/%s%s", protocolName, address, path));
    }

    private SecurityContext getSecurityContext() {
        return new SecurityContext() {

            public boolean isUserInRole(String role) {
                return false;
            }

            public boolean isSecure() {
                return false;
            }

            public Principal getUserPrincipal() {
                return null;
            }

            public String getAuthenticationScheme() {
                return null;
            }
        };
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
            baseUri = "http://" + request.headers().get(HttpHeaderNames.HOST) + ":8080/";
        }
        return baseUri;
    }

    private final  class JerseyResponseWriter implements ContainerResponseWriter {

        private final transient Channel channel;
        private transient DefaultFullHttpResponse response;

        public JerseyResponseWriter(Channel channel) {
            this.channel = channel;
        }

        @Override
        public OutputStream writeStatusAndHeaders(final long contentLength, final ContainerResponse containerResponse) {
            response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.valueOf(containerResponse.getStatus())
            );

            containerResponse.getHttpHeaders().forEach((k,v) -> {
                response.headers().add(k, v);
            });

            return new ByteBufOutputStream(response.content());
        }

        @Override
        public void finish() {
            System.out.println("\n\n\n in response writer respone " + response);
            channel.writeAndFlush(response);
        }
    }

}