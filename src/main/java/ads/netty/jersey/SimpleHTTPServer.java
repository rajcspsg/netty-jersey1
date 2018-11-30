package ads.netty.jersey;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.flow.FlowControlHandler;

public class SimpleHTTPServer {



    public SimpleHTTPServer() {

    }

    public void start() {
        try {
            ServerBootstrap b = new ServerBootstrap();
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            b.group(bossGroup, workerGroup)

                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("codec", new HttpServerCodec());
                            ch.pipeline().addLast("aggregator",
                                    new HttpObjectAggregator(512 * 1024));
                            ch.pipeline().addLast("flowcontroller", new FlowControlHandler());
                            String[] pkgs = new String[] { "me.netty.jersey" };
                            ResourceConfig resourceConfig = new PackagesResourceConfig(pkgs);
                            SimpleHTTPHandler handler = ContainerFactory.createContainer(SimpleHTTPHandler.class, resourceConfig);
                            ch.pipeline().addLast("request",
                                    handler);

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT)
                    .childOption(ChannelOption.AUTO_READ, false)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(5055).sync();
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
