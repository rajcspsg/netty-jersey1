package me.netty.jersey;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.spi.container.WebApplication;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.*;
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

//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
//        boolean keepAlive = HttpUtil.isKeepAlive(request);
//        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
//        ChannelFuture future;
//        if (response!=null) {
//            future = ctx.writeAndFlush(response);
//        } else {
//            future = ctx.channel().closeFuture();
//        }
//        if (!keepAlive) {
//            future.addListener(ChannelFutureListener.CLOSE);
//        }
//    }

//    netty 3.8.0
//            request.getMethod().getName() GET
//    baseUri http://localhost:8888/
//    requestUri http://localhost:8888/imdb/reload
//
//    netty 4.x.x
//request.method().name() GET
//    baseUri http://localhost:8080:8080/
//    fullRequestUri http://0:0:0:0:0:0:0:1:8080/hellonetty

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, FullHttpRequest request) throws Exception {
        System.out.println("request received \n "+ request);

        System.out.println("request.getProtocolVersion().protocolName().toLowerCase() "+ request.getProtocolVersion().protocolName().toLowerCase());
        System.out.println("context.channel().localAddress().toString() "+ context.channel().localAddress().toString());
        System.out.println(request.uri());
        //if (request.uri().equals("/favicon.ico")) return;
        URI applicationURI = createAppURI(
                request.getProtocolVersion().protocolName().toLowerCase(),
                context.channel().localAddress().toString()
        );

        /*URI fullRequestUri = createFullURI(
                request.getProtocolVersion().protocolName().toLowerCase(),
                context.channel().localAddress().toString(),
                request.getUri()
        );*/



        final String base = getBaseUri(request);
        System.out.println("base "+ base);
        URI fullRequestUri = new URI(base + request.uri().substring(1));
        final URI baseUri = new URI(base);
        final URI requestUri = new URI(base.substring(0, base.length() - 1)
                + request.getUri());
        context.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));
        System.out.println("request.method().name() " +request.method().name() + "\n baseUri " + baseUri + " \nfullRequestUri " + fullRequestUri);

        final ContainerRequest cRequest = new ContainerRequest(application, request.method().name(), baseUri,
                fullRequestUri, getHeaders(request), new ByteBufInputStream(request.content()));

        application.handleRequest(cRequest, new JerseyResponseWriter(context));
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
            baseUri = "http://" + request.headers().get(HttpHeaderNames.HOST) + "/";
        }
        return baseUri;
    }

    private final  class JerseyResponseWriter implements ContainerResponseWriter {

        private final transient ChannelHandlerContext channel;
        private transient DefaultFullHttpResponse response;

        public JerseyResponseWriter(ChannelHandlerContext channel) {
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