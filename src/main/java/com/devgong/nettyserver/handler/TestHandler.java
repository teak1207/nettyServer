package com.devgong.nettyserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Base64;


@Slf4j
@Component
@ChannelHandler.Sharable //#1 @Sharable 어노테이션은 여러채널에서 핸들러를 공유 할 수 있음을 나타냄.
@RequiredArgsConstructor
public class TestHandler extends ChannelInboundHandlerAdapter {
    private int DATA_LENGTH = 100;
    /*
    현재는 클라이언트에서 정해진 길이 2048byte를 하나의 패킷으로 읽어오고 있음

    종류
    Inbound Handler	 입력 데이터(in bound)에 대한 변경 상태를 감시하고 처리하는 역할을 하는 핸들러
    Outbound Handler 출력 데이터(out bound)에 대한 동작을 가로채 처리하는 역할을 하는 핸들러
    */
    private ByteBuf buff;

    // 핸들러가 생성될 때 호출되는 메소드
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        buff = ctx.alloc().buffer(DATA_LENGTH);
    }

    // 핸들러가 제거될 때 호출되는 메소드
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        // 클라이언트와 연결되어 트래픽을 생성할 준비가 되었을 때 호출되는 메소드
        buff = null;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 채널이 활성화 됐을 때 호출됨. 데이터를 받거나 보낼 수 있는 상태를 의미함.
        String remoteAddress = ctx.channel().remoteAddress().toString();
        log.info("Remote Address: " + remoteAddress);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /* 클라이언트 -> 서버, 날린 데이터를 받아야하는부분, Inbound buffer에서 읽을 값이 있을 경우 호출 #2
           메세지가 들어올때마다 호출되는 메소드
        */
        ByteBuf mBuf = (ByteBuf) msg;
        buff.writeBytes(mBuf);  // 클라이언트에서 보내는 데이터가 축적됨
        String readMsg = (buff.toString(Charset.defaultCharset()));  // Bytebuf 객체 buff를 String으로 형변환한 값. ---> 이렇게 하면 안됨!!!
        // 클라이언트에서  append 한거를 하나하나 배열에 집어넣으면 되는데ㅐ..

        //System.out.println((char) (buff.getByte(0)) + "***********");
        //System.out.println(buff.isReadable());
        // 읽을 수 있는 바이트가 하나 이상이면 true를 반환

        byte[] bytes = readMsg.getBytes();  //  [r],[e],[c],[o]...
        // String decoded = Base64.getEncoder().encodeToString(bytes);

        // String decoded = new String(bytes);


//        System.out.println("Server received : " + msg);
//        System.out.println("Server received : " + mBuf);
        System.out.println(readMsg);
//        System.out.println(buff.readChar());
//        System.out.println(buff.readInt());


//        System.out.println("-----------------");
//        System.out.println(Base64.getEncoder().encodeToString(bytes));
//        System.out.println("-----------------");
//        System.out.println(Base64.getDecoder().decode(decoded));

        /*for (int i = 0; i < bytes.length; i++) {
            System.out.print(bytes[i] + " ");
            System.out.println();
        }*/

        mBuf.release();

        final ChannelFuture f = ctx.writeAndFlush(buff);
        f.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        ctx.close();
        cause.printStackTrace();
    }
}
