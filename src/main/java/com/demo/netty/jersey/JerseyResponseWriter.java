package com.demo.netty.jersey;

import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.io.OutputStream;

public class JerseyResponseWriter implements ContainerResponseWriter {

    private final transient Channel channel;
    private transient DefaultFullHttpResponse response;

    public JerseyResponseWriter(Channel channel) {
        this.channel = channel;
    }

    @Override
    public OutputStream writeStatusAndHeaders(final long contentLength, final ContainerResponse containerResponse) {
        final ByteBuf byteBuf = Unpooled.directBuffer();
        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.valueOf(response.status().code()), byteBuf);
        return new ByteBufOutputStream(byteBuf);
    }

    @Override
    public void finish() {
        channel.write(response).addListener(ChannelFutureListener.CLOSE);
    }
}
