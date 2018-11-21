package com.demo.netty.jersey;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.core.ResourceConfig;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;

public class NettyServerFactory {

    private NettyServerFactory() {
    }

    public static NettyServer createNettyServer(final ResourceConfig resourceConfig, final URI baseUri) {
        final JerseyHandler jerseyHandler = ContainerFactory.createContainer(JerseyHandler.class, resourceConfig);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        return new NettyServer(getPipelineFactory(jerseyHandler), bossGroup, workerGroup, getLocalSocket(baseUri));
    }

    private static SocketAddress getLocalSocket(final URI baseUri) {
        return new InetSocketAddress(baseUri.getHost(), baseUri.getPort());
    }

    private static JaxRsServerChannelPipelineFactory getPipelineFactory(final JerseyHandler jerseyHandler) {
        return new JaxRsServerChannelPipelineFactory(jerseyHandler);
    }
}
