package com.demo.netty.jersey;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

import java.net.URI;

public class NettyJerseyDemoApp {
    public static void main(String[] args) throws Exception {
        String[] pkgs = new String[] { "com.demo.netty.jersey.resource" };
        ResourceConfig resourceConfig = new PackagesResourceConfig(pkgs);
        URI uri = new URI("http://localhost:8003");
        final NettyServer nettyServer = NettyServerFactory.createNettyServer(resourceConfig, uri);
        nettyServer.startServer();
    }
}
