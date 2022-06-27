package com.devgong.nettyserver.handler;

import com.devgong.nettyserver.domain.PreInstallSetModel;
import com.devgong.nettyserver.service.SensorListService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;


@Slf4j
@Component
@ChannelHandler.Sharable //#1 @Sharable 어노테이션은 여러채널에서 핸들러를 공유 할 수 있음을 나타냄.
@RequiredArgsConstructor
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /*
    현재는 클라이언트에서 정해진 길이 2048 byte 를 하나의 패킷으로 읽어오고 있음

    종류
    Inbound Handler	 입력 데이터(in bound)에 대한 변경 상태를 감시하고 처리하는 역할을 하는 핸들러
    Outbound Handler 출력 데이터(out bound)에 대한 동작을 가로채 처리하는 역할을 하는 핸들러

    <<참고>>
    간단히 파일만 주고 받는 것이 아닌 전문통신을 통해 파일과 더불어 사용자 정보 등 필요한 정보를 짜여진
    protocol 에 맞춰 통신해야 해서 ByteArrayDecoder 를 선택, handler 에서 ByteBuf msg 를 byte[]로
    받아 (dto 에서 parsing 하여  file 을 저장하는 방식으로 개발을 진행했다.)

    */
    private ByteBuf buff;
    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final SensorListService sensorListService;

    //  넘어오는 데이터를 체크하기 위한 model
    // 핸들러가 생성될 때 호출되는 메소드
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        int DATA_LENGTH = 100;
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
        //String remoteAddress = ctx.channel().remoteAddress().toString();
        //log.info("Remote Address: " + remoteAddress);
        System.out.println("===channelActive===");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf mBuf = (ByteBuf) msg;

        System.out.println("-------------------------------");
        System.out.println("Channel Read");
        System.out.println("-------------------------------");

        String flag = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
        String serialNumber = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();
        String datetime = mBuf.readCharSequence(15, Charset.defaultCharset()).toString();
        String paraLen = mBuf.readCharSequence(2, Charset.defaultCharset()).toString();
        String modemNumber = mBuf.readCharSequence(15, Charset.defaultCharset()).toString();
        String debugMsg = mBuf.readCharSequence(2, Charset.defaultCharset()).toString();
        String chkSum = mBuf.readCharSequence(2, Charset.defaultCharset()).toString();

        String totalData = flag + serialNumber + datetime + paraLen + modemNumber + debugMsg + chkSum;

        PreInstallSetModel preInstallDeviceInfos = sensorListService.findData(totalData, modemNumber);



        if (preInstallDeviceInfos != null) {  // 체크썸 값이 맞다면 buff에 write 해라

            System.out.println("++++" + preInstallDeviceInfos);
            buff.writeBytes(preInstallDeviceInfos.getTime1().getBytes());
            buff.writeBytes(preInstallDeviceInfos.getTime2().getBytes());
            buff.writeBytes(preInstallDeviceInfos.getTime3().getBytes());
            buff.writeBytes(preInstallDeviceInfos.getSerialNumber().getBytes());
            buff.writeBytes(preInstallDeviceInfos.getPeriod().getBytes());
            buff.writeBytes(preInstallDeviceInfos.getSamplingTime().getBytes());
            buff.writeBytes(preInstallDeviceInfos.getSampleRate().getBytes());
            buff.writeBytes(preInstallDeviceInfos.getServerUrl().getBytes());
            buff.writeBytes(preInstallDeviceInfos.getServerPort().getBytes());
        } else {
            char nak = '9';
            buff.writeChar(nak);
        }

        ctx.writeAndFlush(buff);
        mBuf.release();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("<<channelReadComplete>>");
        ctx.flush();

        /*ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // 대기중인 메시지를 플러시하고 채널을 닫음
                .addListener(ChannelFutureListener.CLOSE);*/
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
