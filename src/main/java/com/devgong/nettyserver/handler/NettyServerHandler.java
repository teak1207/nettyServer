package com.devgong.nettyserver.handler;

import com.devgong.nettyserver.domain.PreInstallSetModel;
import com.devgong.nettyserver.domain.ReportModel;
import com.devgong.nettyserver.service.SensorListService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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

    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final SensorListService sensorListService;
    final String ack = "8";
    final String nak = "9";

    //  넘어오는 데이터를 체크하기 위한 model
    // 핸들러가 생성될 때 호출되는 메소드
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        // 클라이언트와 연결되어 트래픽을 생성할 준비가 되었을 때 호출되는 메소드
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
                String serialNumber = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();   //char
                String datetime = mBuf.readCharSequence(15, Charset.defaultCharset()).toString();   //char
                String requestType = mBuf.readCharSequence(1, Charset.defaultCharset()).toString(); //char
                String paraLen = mBuf.readCharSequence(2, Charset.defaultCharset()).toString();    //number
                String modemNumber = mBuf.readCharSequence(15, Charset.defaultCharset()).toString(); //number
                String debugMsg = mBuf.readCharSequence(2, Charset.defaultCharset()).toString(); //number
                String chkSum1 = mBuf.readCharSequence(1, Charset.defaultCharset()).toString(); // number
                String chkSum2 = mBuf.readCharSequence(1, Charset.defaultCharset()).toString(); // number
                String convertChk = Integer.toHexString(chkSum1.charAt(0)) + Integer.toHexString(chkSum2.charAt(0));

                String chkData = flag + serialNumber + datetime + requestType + paraLen + modemNumber + debugMsg;

                int convertDecimalSum = 0;

                for (int i = 0; i < chkData.length(); i++) {

                    convertDecimalSum += chkData.charAt(i);    // 문자열 10진수로 바꿔서 저장
                }
                System.out.println("=====================");
                System.out.println(convertDecimalSum);
                System.out.println(convertChk);
                int decimal = Integer.parseInt(convertChk, 16);
                System.out.println(decimal);

                preInstallDeviceInfos = sensorListService.findData(flag, modemNumber);
                System.out.println("[preInstallDeviceInfos] : " + preInstallDeviceInfos.toString());

                if (preInstallDeviceInfos != null) {
                    ctx.write(Unpooled.copiedBuffer(ack.getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getTime1().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getTime2().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getTime3().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getSerialNumber().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getPeriod().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getSamplingTime().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getSampleRate().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getServerUrl().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getServerPort().getBytes()));
                    ctx.flush();
                    mBuf.release();
                } else {
                    ctx.writeAndFlush(Unpooled.copiedBuffer(nak.getBytes()));
                    mBuf.release();
                }
            }
            /* === [ REPORT PROCESS RECEIVE START ] === */
            if (flag.equals("8") || flag.equals("9")) {

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

                boolean reportResult = sensorListService.insertReport(reportModel);

                if (reportResult) {  // 체크썸 값이 맞다면 buff에 write
                    ctx.writeAndFlush(Unpooled.copiedBuffer(ack.getBytes()));
                    mBuf.release();
                } else {
                    ctx.writeAndFlush(Unpooled.copiedBuffer(nak.getBytes()));
                    mBuf.release();
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
