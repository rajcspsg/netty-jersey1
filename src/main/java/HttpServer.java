import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class HttpServer {
    public static final int PORT = 8080;
    public static final String host = "127.0.0.1";

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private final ServerBootstrap bootstrap;

    public HttpServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        String[] pkgs = new String[] { "me.netty.jersey" };
        ResourceConfig resourceConfig = new PackagesResourceConfig(pkgs);
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpServerInitializer(resourceConfig));
    }

    public void startSync() throws Exception {

        try {
            ChannelFuture channelFuture = bootstrap.bind(PORT).sync();
            //wait until the server socket is closed.
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static SocketAddress getLocalSocket() {
        return new InetSocketAddress(host, PORT );
    }

    public void startAsync() {

        bootstrap.bind(getLocalSocket());

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }

        });
    }

    public static void main(String[] args) throws Exception {
        new HttpServer().startSync();
    }
}
