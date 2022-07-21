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
        PreinstallReportModel preinstallReportModel = new PreinstallReportModel();
        SettingInitModel settingInitModel = new SettingInitModel();

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

                System.out.println("[convertDecimalSum] : " + convertDecimalSum);
                System.out.println("[convertChk] : " + convertChk);


                int decimal = Integer.parseInt(convertChk, 16);
                System.out.println("[decimal] : " + decimal);

                //  convertDecimalSum 와  decimal 이게 같으면 진행하고 아니면 재요청
                if (convertDecimalSum == decimal) {
                    System.out.println("[CheckSum] : SUCCESS :)");
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
                        report = true;
                    } else {
                        ctx.writeAndFlush(Unpooled.copiedBuffer(nak.getBytes()));
                        mBuf.release();
                    }
                } else {
                    System.out.println("[CheckSum][FAIL] : Not Accurate  ");
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

                preinstallReportModel.setSerialNumber(serialNum);
                preinstallReportModel.setDateTime(datetime);
                preinstallReportModel.setDebugMsg(debugMessage);
                preinstallReportModel.setRecordingTime1(recordingTime1);
                preinstallReportModel.setRecordingTime2(recordingTime2);
                preinstallReportModel.setRecordingTime3(recordingTime3);
                preinstallReportModel.setFirmWareVersion(firmWareVersion);
                preinstallReportModel.setBatteryVtg(batteryVtg);
                preinstallReportModel.setRSSI(RSSI);
                preinstallReportModel.setSamplingTime(samplingTime);
                preinstallReportModel.setSamplingRate(samplingRate);
                preinstallReportModel.setModemNumber(modemNumber);
                preinstallReportModel.setProject(project);
                preinstallReportModel.setSid(sid);
                preinstallReportModel.setPeriod(period);
                preinstallReportModel.setServerUrl(serverUrl);
                preinstallReportModel.setServerPort(serverPort);

                boolean reportResult = sensorListService.insertReport(preinstallReportModel);

                if (reportResult) {  // 체크썸 값이 맞다면 buff에 write
                    ctx.writeAndFlush(Unpooled.copiedBuffer(ack.getBytes()));
                    mBuf.release();
                } else {
                    ctx.writeAndFlush(Unpooled.copiedBuffer(nak.getBytes()));
                    mBuf.release();
                }
                // setting process
            } else if (flag.equals("1")) {


                String serialNum = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();
                String datetime = mBuf.readCharSequence(15, Charset.defaultCharset()).toString();
                String paraLen = mBuf.readCharSequence(2, Charset.defaultCharset()).toString();
                String settingRecordingtime1 = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String settingRecordingtime2 = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String settingRecordingtime3 = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String period = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();

/*
                String period2 = "";
                if (period1.equals("0") || period1.equals("1") || period1.equals("5")) {  // readerIndex의 값이 0  or 1 or 5 시작하면
                } else {   // readIndex의 값이 10 or 30 or 60 시작하면
                    period2 = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();

                }
*/

                String samplingTime = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String sampleRate = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String sleep = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String active = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String factoryReset = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String Px = mBuf.readCharSequence(10, Charset.defaultCharset()).toString();
                String Py = mBuf.readCharSequence(10, Charset.defaultCharset()).toString();
                String serverUrl = mBuf.readCharSequence(32, Charset.defaultCharset()).toString();
                String serverPort = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();

//                String test = period1 + period2;

                System.out.println(serialNum);
                System.out.println(datetime);
                System.out.println(paraLen);
                System.out.println(settingRecordingtime1);
                System.out.println(settingRecordingtime2);
                System.out.println(settingRecordingtime3);
                System.out.println(period);
                System.out.println(samplingTime);
                System.out.println(sampleRate);
                System.out.println(sleep);
                System.out.println(active);
                System.out.println(factoryReset);
                System.out.println(Px);
                System.out.println(Py);
                System.out.println(serverUrl);
                System.out.println(serverPort);


/*                settingInitModel.setRecordingTime1(settingRecordingtime1);
                settingInitModel.setRecordingTime2(settingRecordingtime2);
                settingInitModel.setRecordingTime3(settingRecordingtime3);
                settingInitModel.setPeriod(period);
                settingInitModel.setSamplerate(sampleRate);
                settingInitModel.setSample(samplingTime);
                settingInitModel.setSleep(sleep);
                settingInitModel.setActive(active);*/

//                System.out.println(settingInitModel.toString());


            }



            /*else{
                System.out.println("멍청이");
            }*/
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        // request process
        // data process


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
