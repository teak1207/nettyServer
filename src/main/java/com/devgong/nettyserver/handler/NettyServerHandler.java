package com.devgong.nettyserver.handler;

import com.devgong.nettyserver.domain.PreInstallSetModel;
import com.devgong.nettyserver.domain.PreinstallReportModel;
import com.devgong.nettyserver.domain.SettingInitModel;
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
    boolean report = false;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf mBuf = (ByteBuf) msg;

        System.out.println("Channel Read");
        System.out.println("===================");

        String flag = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();

        PreInstallSetModel preInstallDeviceInfos = null;
        PreinstallReportModel preinstallReportModel = new PreinstallReportModel();
        SettingInitModel settingInitModel = new SettingInitModel();

        // 플래그에 값에 따라 분기
        try {
            if (flag.equals("A")) {
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

                int decimal = Integer.parseInt(convertChk, 16);
                System.out.println("[preinstall 넘어온값] : " + chkData);
                System.out.println("=====================");
                System.out.println("[chkSum1] : " + chkSum1);
                System.out.println("[chkSum2] : " + chkSum2);
                System.out.println("[convertDecimalSum] : " + convertDecimalSum);
                System.out.println("[convertChk] : " + convertChk);
                System.out.println("[decimal] : " + decimal);
                System.out.println("=====================");

                //  convertDecimalSum 와  decimal 이게 같으면 진행하고 아니면 재요청
                if (convertDecimalSum == decimal) {
                    System.out.println("[CheckSum] : SUCCESS :)");
                    preInstallDeviceInfos = sensorListService.findData(flag, modemNumber);
                    System.out.println("[preInstallDeviceInfos] : " + preInstallDeviceInfos.toString());
                }
                if (preInstallDeviceInfos != null) {
                    ctx.write(Unpooled.copiedBuffer(ack.getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getTime1().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getTime2().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getTime3().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getFmFrequency().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getSid().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getPname().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getPx().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getPy().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getSerialNumber().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getPeriod().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getSamplingTime().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getSampleRate().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getServerUrl().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getServerPort().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getDbUrl().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getDbPort().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getRadioTime().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getBaudrate().getBytes()));
/*                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getDbPort().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getRadioTime().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(preInstallDeviceInfos.getBaudrate().getBytes()));*/
                    ctx.flush();
                    mBuf.release();
                    report = true;
                } else {
                    ctx.writeAndFlush(Unpooled.copiedBuffer(nak.getBytes()));
                    System.out.println("[CheckSum][FAIL] : Not Accurate");
                    mBuf.release();
                }

            } else if ((flag.equals("8") || flag.equals("9")) && report == true) {
                /* === [ REPORT PROCESS RECEIVE START ] === */
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
                String fmRadio = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String firmWareVersion = mBuf.readCharSequence(6, Charset.defaultCharset()).toString();
                String batteryVtg = mBuf.readCharSequence(5, Charset.defaultCharset()).toString();
                String RSSI = mBuf.readCharSequence(1, Charset.defaultCharset()).toString(); //Number
                String deviceStatus = mBuf.readCharSequence(2, Charset.defaultCharset()).toString();
                String samplingTime = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();//Number
                String px = mBuf.readCharSequence(10, Charset.defaultCharset()).toString();
                String py = mBuf.readCharSequence(10, Charset.defaultCharset()).toString();
                String modemNumber = mBuf.readCharSequence(12, Charset.defaultCharset()).toString();
                String sid = mBuf.readCharSequence(16, Charset.defaultCharset()).toString(); //*
                String period = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String serverUrl = mBuf.readCharSequence(32, Charset.defaultCharset()).toString(); //*
                String serverPort = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String DBUrl = mBuf.readCharSequence(32, Charset.defaultCharset()).toString(); //*
                String DBPort = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String baudrate = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();//Number
                String baudrateNext = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();//Number
                String pcbVersion = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();


                preinstallReportModel.setSerialNumber(serialNum);
                preinstallReportModel.setDateTime(datetime);
                preinstallReportModel.setDebugMsg(debugMessage);
                preinstallReportModel.setRecordingTime1(recordingTime1);
                preinstallReportModel.setRecordingTime2(recordingTime2);
                preinstallReportModel.setRecordingTime3(recordingTime3);
                preinstallReportModel.setFmRadio(fmRadio);
                preinstallReportModel.setFirmWareVersion(firmWareVersion);
                preinstallReportModel.setBatteryVtg(batteryVtg);
                preinstallReportModel.setRSSI(RSSI);
                preinstallReportModel.setDeviceStatus(deviceStatus);
                preinstallReportModel.setSamplingTime(samplingTime);
                preinstallReportModel.setPx(px);
                preinstallReportModel.setPy(py);

                preinstallReportModel.setModemNumber(modemNumber);
                preinstallReportModel.setSid(sid);
                preinstallReportModel.setPeriod(period);
                preinstallReportModel.setServerUrl(serverUrl);
                preinstallReportModel.setServerPort(serverPort);
                preinstallReportModel.setDbUrl(DBUrl);
                preinstallReportModel.setDbPort(DBPort);
                preinstallReportModel.setBaudrate(baudrate);
                preinstallReportModel.setPcbVersion(pcbVersion);

                System.out.println("[DB에 들어갈 값]" + preinstallReportModel.toString());


                boolean reportResult = sensorListService.insertReport(preinstallReportModel);

                if (reportResult) {  // 체크썸 값이 맞다면 buff에 write
//                    System.out.println(ack);
//                    ctx.writeAndFlush(Unpooled.copiedBuffer(ByteBuffer.allocateDirect(ack)));
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
    public void channelReadComplete(ChannelHandlerContext ctx) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
