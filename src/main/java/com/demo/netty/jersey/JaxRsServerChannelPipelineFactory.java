package com.demo.netty.jersey;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;

public class JaxRsServerChannelPipelineFactory extends ChannelInitializer<Channel> {

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
    protected void initChannel(Channel ch) {
        pipeline = ch.pipeline();
        ch.pipeline().addLast("codec", new HttpServerCodec());
        pipeline.addLast("jerseyHandler", jerseyHandler);
    }
}