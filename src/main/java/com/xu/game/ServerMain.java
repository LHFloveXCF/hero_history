package com.xu.game;

import com.xu.game.protocol.ProtoDecoder;
import com.xu.game.protocol.ProtoEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hero_history
 */
public class ServerMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();

        ChannelFuture f = b.group(boss, worker).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast(new HttpServerCodec())
                        .addLast(new HttpObjectAggregator(65535))
                        .addLast(new WebSocketServerProtocolHandler("/websocket"))
                        .addLast(new ProtoDecoder())
                        .addLast(new ProtoEncoder())
                        .addLast(new ServerHandler());
            }
        }).bind(9999);

        try {
            f.sync();
            // System.out.println("服务器启动成功@@");
            LOGGER.info("服务器启动成功@@");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}
