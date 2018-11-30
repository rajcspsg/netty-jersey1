package ads.netty.jersey;

import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import java.io.OutputStream;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public final class JerseyResponseWriter implements ContainerResponseWriter {

    private final ChannelHandlerContext ctx;
    private DefaultFullHttpResponse response;

    JerseyResponseWriter(final ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public OutputStream writeStatusAndHeaders(long contentLength, ContainerResponse containerResponse) {
        response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer("My Netty".getBytes()), false);
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        containerResponse.getHttpHeaders().forEach((k,v) -> {
            response.headers().add(k, v);
        });
        return new ByteBufOutputStream(response.content());
    }

    @Override
    public void finish() {

    }
}
