package ads.netty.jersey;

import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.io.OutputStream;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public final class JerseyResponseWriter implements ContainerResponseWriter {

    private final ChannelHandlerContext ctx;
    private transient  DefaultFullHttpResponse response;

    JerseyResponseWriter(final ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public OutputStream writeStatusAndHeaders(long contentLength, ContainerResponse containerResponse) {
        System.out.println("containerResponse.getStatus() " + containerResponse.getStatus());
        System.out.println(" containerResponse.getResponse().getEntity() " + containerResponse.getResponse().getEntity());
        response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(containerResponse.getStatus()), Unpooled.copiedBuffer(containerResponse.getResponse().getEntity().toString().getBytes()) );
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        containerResponse.getHttpHeaders().forEach((k,v) -> {
            response.headers().add(k, v);
        });
        ctx.writeAndFlush(response);
        return new ByteBufOutputStream(response.content());
    }

    @Override
    public void finish() {
        System.out.println("\n\n\n in response writer response " + response);

        ctx.close();

    }
}
