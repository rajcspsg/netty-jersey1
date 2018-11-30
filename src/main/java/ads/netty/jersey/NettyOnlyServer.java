package ads.netty.jersey;

public class NettyOnlyServer {
    public static void main(String[] args) {
        SimpleHTTPServer hs = new SimpleHTTPServer();
        hs.start();
    }
}
