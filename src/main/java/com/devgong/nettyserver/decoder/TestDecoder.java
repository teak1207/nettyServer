package com.devgong.nettyserver.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class TestDecoder extends ByteToMessageDecoder {
    private int DATA_LENGTH = 2048; // 정해진 길이만큼 데이터가 들어올 때까지 wait

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < DATA_LENGTH) {
            return;
        }
//        System.out.println( "****test******"+ in.readableBytes());
        out.add(in.readBytes(DATA_LENGTH));
    }
        // *** ==여기서 DB로 넘겨줘야하는건가? ==> DB로 넘기려면 바이트 단위로 자르고 변수에 초기화 ==> JDBC? JPA를 쓸건지?

}
