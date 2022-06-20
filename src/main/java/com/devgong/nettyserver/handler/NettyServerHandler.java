package com.devgong.nettyserver.handler;

import com.devgong.nettyserver.domain.PreInstallSetModel;
import com.devgong.nettyserver.service.SensorListService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
    현재는 클라이언트에서 정해진 길이 2048byte를 하나의 패킷으로 읽어오고 있음

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
    // private SensorListModel sensorListModel = new SensorListModel();
//    private PreInstallCheckModel preInstallCheckModel = new PreInstallCheckModel();
    private final SensorListService sensorListService;

    //  넘어오는 데이터를 체크하기 위한 model

    // private SensorListRepository sensorListRepository;

    // 핸들러가 생성될 때 호출되는 메소드
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

//        System.out.println("handlerAdded Of [SERVER]");
//        Channel incoming = ctx.channel();
//        for(Channel channel : channelGroup){
//
//        }
        log.info("");
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
        //byte[] bytes = readMsg.getBytes();  //  [r],[e],[c],[o]...
        System.out.println("-----------------");
        System.out.println("===channelRead===");
        System.out.println("-----------------");
        char flag = readMsg.charAt(0);// A , flag
        String serialNumber = readMsg.substring(1, 25);
        String dateTime = readMsg.substring(25, 40);
        String paraLen = readMsg.substring(40, 44); // 00ff
        String modemNumber = readMsg.substring(44, 59);
        String debugMsg = readMsg.substring(59, 61);
        String chksum = readMsg.substring(61, 63);
        String totalData = flag + serialNumber + dateTime + paraLen + modemNumber + debugMsg;


        System.out.println("-----------------");
        System.out.println("**" + readMsg.substring(0, 60));
        System.out.println("**" + totalData);
        System.out.println("**" + totalData.length());


        PreInstallSetModel preInstallDeviceInfos = sensorListService.findData(totalData, modemNumber);
        //  return 되는 sensorListModel (Object)을 담음.
        if (preInstallDeviceInfos != null) {
            System.out.println("****" + preInstallDeviceInfos);

        } else {
            char nak = '9';
            System.out.println(nak);
        }

        // PreInstall 값 or NAK

        ctx.writeAndFlush("<<test>>" + preInstallDeviceInfos);

        mBuf.release();
        final ChannelFuture f = ctx.writeAndFlush(buff);
        f.addListener(ChannelFutureListener.CLOSE);


    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("<<channelReadComplete>>");
        ctx.flush();

        /*ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // 대기중인 메시지를 플러시하고 채널을 닫음
                .addListener(ChannelFutureListener.CLOSE);*/
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
