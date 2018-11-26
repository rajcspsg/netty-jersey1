package com.demo.netty.jersey;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class JaxRsServerChannelPipelineFactory extends ChannelInitializer<SocketChannel> {

    private JerseyHandler jerseyHandler;
    private ChannelPipeline pipeline;

    public JerseyHandler getJerseyHandler() {
        return jerseyHandler;
    }

    public JaxRsServerChannelPipelineFactory(final JerseyHandler jerseyHandler) {
        this.jerseyHandler = jerseyHandler;
    }

    public void setJerseyHandler(JerseyHandler jerseyHandler) {
        this.jerseyHandler = jerseyHandler;
    }

    public ChannelPipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(ChannelPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        pipeline = ch.pipeline();
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(1024));
        pipeline.addLast("jerseyHandler", jerseyHandler);

    }
}