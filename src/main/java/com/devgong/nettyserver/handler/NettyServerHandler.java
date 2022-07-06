package com.devgong.nettyserver.handler;

import com.devgong.nettyserver.domain.PreInstallSetModel;
import com.devgong.nettyserver.domain.ReportModel;
import com.devgong.nettyserver.service.SensorListService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
    <<종류>>
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

    final char ack = '8';
    final char nak = '9';

    //  넘어오는 데이터를 체크하기 위한 model
    // 핸들러가 생성될 때 호출되는 메소드
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        int DATA_LENGTH = 100;
        buff = ctx.alloc().buffer(DATA_LENGTH);
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        // 클라이언트와 연결되어 트래픽을 생성할 준비가 되었을 때 호출되는 메소드
        buff = null;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 채널이 활성화 됐을 때 호출됨. 데이터를 받거나 보낼 수 있는 상태를 의미함.

        System.out.println("===channelActive===");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ByteBuf mBuf = (ByteBuf) msg;


        System.out.println("===================");
        System.out.println("Channel Read");
        System.out.println("===================");

        String flag = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();

        PreInstallSetModel preInstallDeviceInfos = null;
        ReportModel reportModel = new ReportModel();

        // 플래그에 값에 따라 분기
        try {
            if (flag.equals("0")) {
                //  *** 주의 *** 밑 preinstall 프로토콜항목 순서를 바꾸면 안됨.
                String serialNumber = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();
                String datetime = mBuf.readCharSequence(15, Charset.defaultCharset()).toString();
                String paraLen = mBuf.readCharSequence(2, Charset.defaultCharset()).toString();
                String modemNumber = mBuf.readCharSequence(15, Charset.defaultCharset()).toString();
                String debugMsg = mBuf.readCharSequence(2, Charset.defaultCharset()).toString();
                String chkSum = mBuf.readCharSequence(2, Charset.defaultCharset()).toString();

                preInstallDeviceInfos = sensorListService.findData(flag, modemNumber);
                System.out.println("[preInstallDeviceInfos] : " + preInstallDeviceInfos.toString());

                if (preInstallDeviceInfos != null) {
                    buff.writeChar(ack);
                    buff.writeBytes(preInstallDeviceInfos.getTime1().getBytes());
                    buff.writeBytes(preInstallDeviceInfos.getTime2().getBytes());
                    buff.writeBytes(preInstallDeviceInfos.getTime3().getBytes());
                    buff.writeBytes(preInstallDeviceInfos.getSerialNumber().getBytes());
                    buff.writeBytes(preInstallDeviceInfos.getPeriod().getBytes());
                    buff.writeBytes(preInstallDeviceInfos.getSamplingTime().getBytes());
                    buff.writeBytes(preInstallDeviceInfos.getSampleRate().getBytes());
                    buff.writeBytes(preInstallDeviceInfos.getServerUrl().getBytes());
                    buff.writeBytes(preInstallDeviceInfos.getServerPort().getBytes());
                    ctx.writeAndFlush(buff);
                    mBuf.release();
                } else {
                    buff.writeChar(nak); //0 이 아니며
                }
            }

            /* === [ REPORT PROCESS RECEIVE START ] === */
            if (flag.equals("8") || flag.equals("9")) {

//                int x = (mBuf.readCharSequence(41, Charset.defaultCharset())).length();

                //  *** 주의 *** 밑 report 프로토콜항목 순서를 바꾸면 안됨.
                // report 값을 바이트크기에 따라 분할 후, 변수 저장.
                /*==== Header ====*/
                String serialNum = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();
                String datetime = mBuf.readCharSequence(15, Charset.defaultCharset()).toString();
                String paraLen = mBuf.readCharSequence(2, Charset.defaultCharset()).toString();
                /*==== Body ====*/
                String debugMessage = mBuf.readCharSequence(2, Charset.defaultCharset()).toString();

                String recordingTime1 = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String recordingTime2 = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String recordingTime3 = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String firmWareVersion = mBuf.readCharSequence(6, Charset.defaultCharset()).toString();
                String batteryVtg = mBuf.readCharSequence(5, Charset.defaultCharset()).toString();
                String RSSI = mBuf.readCharSequence(1, Charset.defaultCharset()).toString(); //Number
                String samplingTime = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();//Number
                String samplingRate = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();//Number
                String modemNumber = mBuf.readCharSequence(15, Charset.defaultCharset()).toString();
                String project = mBuf.readCharSequence(32, Charset.defaultCharset()).toString(); //*
                String sid = mBuf.readCharSequence(16, Charset.defaultCharset()).toString(); //*
                String period = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();//Number
                String serverUrl = mBuf.readCharSequence(32, Charset.defaultCharset()).toString(); //*
                String serverPort = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String chksum = mBuf.readCharSequence(2, Charset.defaultCharset()).toString();

                reportModel.setSerialNumber(serialNum);
                reportModel.setDateTime(datetime);
                reportModel.setDebugMsg(debugMessage);
                reportModel.setRecordingTime1(recordingTime1);
                reportModel.setRecordingTime2(recordingTime2);
                reportModel.setRecordingTime3(recordingTime3);
                reportModel.setFirmWareVersion(firmWareVersion);
                reportModel.setBatteryVtg(batteryVtg);
                reportModel.setRSSI(RSSI);
                reportModel.setSamplingTime(samplingTime);
                reportModel.setSamplingRate(samplingRate);
                reportModel.setModemNumber(modemNumber);
                reportModel.setProject(project);
                reportModel.setSid(sid);
                reportModel.setPeriod(period);
                reportModel.setServerUrl(serverUrl);
                reportModel.setServerPort(serverPort);

                System.out.println(reportModel.toString());
                boolean reportResult = sensorListService.insertReport(reportModel);
                System.out.println(reportResult);

                /*
                  if (reportResult == true) {  // 체크썸 값이 맞다면 buff에 write
                    buff.writeChar(ack); //0 이 아니며
                    ctx.writeAndFlush(buff);
                    mBuf.release();
                } else {
                    buff.writeChar(nak);
                    ctx.writeAndFlush(buff);
                }*/
            }


        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
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
