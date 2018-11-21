package com.demo.netty.jersey;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

public class NettyServer {

    public static final String PROPERTY_BASE_URI = "com.devsprint.jersey.api.container.netty.baseUri";

    private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);

    private NettyServer() {
    }

    public NettyServer(JaxRsServerChannelPipelineFactory pipeline, EventLoopGroup bossGroup, EventLoopGroup workerGroup,
                       SocketAddress socketAddress) {
        this.pipeline = pipeline;
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.socketAddress = socketAddress;
        this.serverBootstrap = buildBootstrap(pipeline, bossGroup, workerGroup);
    }

    private JaxRsServerChannelPipelineFactory pipeline;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private SocketAddress socketAddress;

    public JaxRsServerChannelPipelineFactory getPipeline() {
        return pipeline;
    }

    public void setPipeline(JaxRsServerChannelPipelineFactory pipeline) {
        this.pipeline = pipeline;
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public void setBossGroup(EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public void setWorkerGroup(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
    }

    public ServerBootstrap getServerBootstrap() {
        return serverBootstrap;
    }

    public void setServerBootstrap(ServerBootstrap serverBootstrap) {
        this.serverBootstrap = serverBootstrap;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    private ServerBootstrap buildBootstrap(JaxRsServerChannelPipelineFactory pipeline, EventLoopGroup bossGroup,
                                           EventLoopGroup workerGroup) {
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(pipeline)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT)
                .childOption(ChannelOption.AUTO_READ, false).childOption(ChannelOption.SO_KEEPALIVE, true);
        return serverBootstrap;
    }

    public void startServer() throws InterruptedException {
        ChannelFuture cf = serverBootstrap.bind(socketAddress).sync();
        cf.channel().closeFuture().sync();
    }

    public void startServerAsync() throws InterruptedException {
        ChannelFuture cf = serverBootstrap.bind(socketAddress);
        cf.channel().closeFuture();
    }

    public void stopServer() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        // serverBootstrap
    }
}
