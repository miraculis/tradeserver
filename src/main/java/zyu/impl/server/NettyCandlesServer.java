package zyu.impl.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import zyu.candles.Candle;

import java.util.Collection;
import java.util.function.ToIntFunction;

/**
 * Created by miraculis on 03.05.2017.
 */
public class NettyCandlesServer {
    private final ObjectMapper mapper = new ObjectMapper();
    private final int port;
    private final ToIntFunction<ToIntFunction<Collection<Candle>>> provider;

    public NettyCandlesServer(int port, ToIntFunction<ToIntFunction<Collection<Candle>>> provider) {
        this.port = port;
        this.provider = provider;
        mapper.registerModule(new JSR310Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MessageToByteEncoder<Collection<Candle>>() {
                                @Override
                                protected void encode(ChannelHandlerContext ctx, Collection<Candle> msg, ByteBuf out) throws Exception {
                                }
                            });
                            provider.applyAsInt((x) -> {
                                ch.pipeline().writeAndFlush(encode(x, Unpooled.buffer(1024)));
                                return 0;
                            });
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            ChannelFuture f = null; // (7)
            try {
                f = b.bind(port).sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }

            try {
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private ByteBuf encode(Collection<Candle> x, ByteBuf b) {
        x.forEach((y)->{
            byte[] json = null;
            try {
                json = mapper.writeValueAsBytes(y);
            } catch (JsonProcessingException e) {
                System.out.println("Skip candle " + x);
            }
            b.writeBytes(json);
            b.writeBytes("\r\n".getBytes());
        });
        return b;
    }
}
