package com.devgong.nettyserver.socket;

import com.devgong.nettyserver.decoder.TestDecoder;
import com.devgong.nettyserver.handler.NettyServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyServerHandler nettyServerHandler;
    // 클라이언트 소켓 채널이 생성될 때 호출
    //private final SslContext sslCtx;


    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(2048)); //set  buf size here


//        ch.config().setReceiveBufferSize(1024);
        // decoder는 @Sharable이 안 됨, Bean 객체 주입이 안 되고, 매번 새로운 객체 생성해야 함
        TestDecoder testDecoder = new TestDecoder();

        pipeline.addLast(nettyServerHandler);
        pipeline.addLast(new DelimiterBasedFrameDecoder(2048, Delimiters.lineDelimiter()));


//        pipeline.addLast(new StringEncoder());
        // 뒤이어 처리할 디코더 및 핸들러 추가
        pipeline.addLast(testDecoder);


    }
}
