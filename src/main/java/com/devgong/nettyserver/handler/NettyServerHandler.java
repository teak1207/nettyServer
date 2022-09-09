package com.devgong.nettyserver.handler;

import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.service.DataSensorListService;
import com.devgong.nettyserver.service.PreinstallSensorListService;
import com.devgong.nettyserver.service.RequestSensorListService;
import com.devgong.nettyserver.service.SettingSensorListService;
import com.devgong.nettyserver.util.CalcCheckSum;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
@Component
@ChannelHandler.Sharable //#1 @Sharable 어노테이션은 여러채널에서 핸들러를 공유 할 수 있음을 나타냄.
@RequiredArgsConstructor
public class NettyServerHandler extends ChannelInboundHandlerAdapter {


    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final PreinstallSensorListService preinstallSensorListService;
    private final SettingSensorListService settingSensorListService;
    private final DataSensorListService dataSensorListService;
    private final RequestSensorListService requestSensorListService;

    CalcCheckSum calcCheckSum = new CalcCheckSum();
    final byte[] ack = {8};
    final byte[] nak = {9};
    final byte preInstallFlag = 'A';

    final byte ServerToDevice = 'S';

    DataRefModel dataRefModel = new DataRefModel();
    static int framesize;

    public byte[] intToByte(int value) {
        byte[] reValue;
        reValue = new byte[4];

        reValue[3] = (byte) (value);
        reValue[2] = (byte) (value >> 8);
        reValue[1] = (byte) (value >> 16);
        reValue[0] = (byte) (value >> 24);
        return reValue;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf mBuf = (ByteBuf) msg;

        System.out.println("Channel Read");
        System.out.println("===================");

        String flag = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();

        PreInstallSetModel preInstallDeviceInfos = null;
        SettingSetModel settingDeviceInfos = null;
        PreinstallReportModel preinstallReportModel = new PreinstallReportModel();

        DataInsertModel dataInsertModel = new DataInsertModel();
        PreInstallSensorListAllModel reportFindResults = new PreInstallSensorListAllModel();
        RequestListAllModel requestFindResults;

        /* 플래그에 값에 따라 분기*/
        /*  <<< Pre-Install Step >>> ===========================================================================================*/
        try {
            log.info("FLAG : {}", flag);

            if (flag.equals("A")) {
                String serialNumber = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();   //char
                String datetime = mBuf.readCharSequence(15, Charset.defaultCharset()).toString();   //char
                String requestType = mBuf.readCharSequence(1, Charset.defaultCharset()).toString(); //char
                String paraLen = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();    //number
                String modemNumber = mBuf.readCharSequence(16, Charset.defaultCharset()).toString().trim(); //number
                String debugMsg = mBuf.readCharSequence(13, Charset.defaultCharset()).toString(); //number
                byte chkSum1 = (mBuf.readByte());
                byte chkSum2 = (mBuf.readByte());

                String convertChk = String.format("%x%x", chkSum1, chkSum2);
                String chkData = flag + serialNumber + datetime + requestType + paraLen + modemNumber + debugMsg;

                int convertDecimalSum = 0;

                for (int i = 0; i < chkData.length(); i++) {
                    convertDecimalSum += chkData.charAt(i);    // 문자열 10진수로 바꿔서 저장
                }

                int decimal = Integer.parseInt(convertChk, 16);

                System.out.println("chkData=" + chkData);
                System.out.println("=====================");
                System.out.println("[chkSum1]:" + chkSum1);
                System.out.println("[chkSum2]:" + chkSum2);
                System.out.println("[convertDecimalSum]:" + convertDecimalSum);
                System.out.println("[convertChk]:" + convertChk);
                System.out.println("[decimal]:" + decimal);
                System.out.println("=====================");

                //  convertDecimalSum 와  decimal 이게 같으면 진행하고 아니면 재요청
//                if (convertDecimalSum == decimal) {
                if (true) {
                    System.out.println("[CheckSum] : SUCCESS :)");
                    preInstallDeviceInfos = preinstallSensorListService.preInstallfindData(flag, modemNumber);
                    log.info("[preInstallDeviceInfos] : " + preInstallDeviceInfos.toString());
                }

                PreInstallResponseModel preInstallResponseModel = PreInstallResponseModel.builder()
                        .recordTime1(preInstallDeviceInfos.getTime1().toCharArray())
                        .recordTime2(preInstallDeviceInfos.getTime2().toCharArray())
                        .recordTime3(preInstallDeviceInfos.getTime3().toCharArray())
                        .fmFrequency(preInstallDeviceInfos.getFmFrequency().toCharArray())
                        .sid(preInstallDeviceInfos.getSid().toCharArray())
                        .pname(preInstallDeviceInfos.getPname().toCharArray())
                        .px(preInstallDeviceInfos.getPx().toCharArray())
                        .py(preInstallDeviceInfos.getPy().toCharArray())
                        .serialNumber(preInstallDeviceInfos.getSerialNumber().toCharArray())
                        .period(preInstallDeviceInfos.getPeriod().toCharArray())
                        .samplingTime(preInstallDeviceInfos.getSamplingTime().toCharArray())
                        .samplerate(preInstallDeviceInfos.getSampleRate().toCharArray())
                        .serverUrl(preInstallDeviceInfos.getServerUrl().toCharArray())
                        .serverPort(preInstallDeviceInfos.getServerPort().toCharArray())
                        .dbUrl(preInstallDeviceInfos.getDbUrl().toCharArray())
                        .dbPort(preInstallDeviceInfos.getDbPort().toCharArray())
                        .radioTime(preInstallDeviceInfos.getRadioTime().toCharArray())
                        .baudrate(preInstallDeviceInfos.getBaudrate().toCharArray()).build();


                int headerLength = flag.length() + serialNumber.length() + datetime.length() + requestType.length() + paraLen.length();


                int preinstallLength = preInstallDeviceInfos.getTime1().length() + preInstallDeviceInfos.getTime2().length() + preInstallDeviceInfos.getTime3().length() +
                        preInstallDeviceInfos.getFmFrequency().length() + preInstallDeviceInfos.getSid().length() + preInstallDeviceInfos.getPname().length() +
                        preInstallDeviceInfos.getPx().length() + preInstallDeviceInfos.getPy().length() + preInstallDeviceInfos.getSerialNumber().length() +
                        preInstallDeviceInfos.getPeriod().length() + preInstallDeviceInfos.getSamplingTime().length() + preInstallDeviceInfos.getSampleRate().length() +
                        preInstallDeviceInfos.getServerUrl().length() + preInstallDeviceInfos.getServerPort().length() + preInstallDeviceInfos.getDbUrl().length() +
                        preInstallDeviceInfos.getDbPort().length() + preInstallDeviceInfos.getRadioTime().length() + preInstallDeviceInfos.getBaudrate().length() + "tetesttesttesttesttesttesttesttesttesttesttesttestte".length();

                String preInstallReport = preInstallDeviceInfos.getTime1() + preInstallDeviceInfos.getTime2() + preInstallDeviceInfos.getTime3() +
                        preInstallDeviceInfos.getPx() + preInstallDeviceInfos.getPy() + preInstallDeviceInfos.getSerialNumber() +
                        preInstallDeviceInfos.getPeriod() + preInstallDeviceInfos.getSamplingTime() + preInstallDeviceInfos.getSampleRate() +
                        preInstallDeviceInfos.getServerUrl() + preInstallDeviceInfos.getServerPort() + preInstallDeviceInfos.getDbUrl() +
                        preInstallDeviceInfos.getDbPort() + preInstallDeviceInfos.getRadioTime() + preInstallDeviceInfos.getBaudrate() + "testtestteststtesttesttesttesttesttesttesttesttestte";


                byte[] totalData = preInstallReport.getBytes();
                byte[] chkSumData = calcCheckSum.makeChecksum(preInstallReport);
                int arrayLength = totalData.length + chkSumData.length;

                System.out.println("preinstallLength->" + preinstallLength);
                System.out.println("preInstallReport->" + preInstallReport);
                System.out.println("arrayLength->" + arrayLength);
                System.out.println("chkSumData->" + new String(chkSumData));
                System.out.println("headerLength->" + headerLength);


                if (preInstallDeviceInfos != null) {
                    ctx.write(Unpooled.copiedBuffer(new byte[]{preInstallFlag}));
                    ctx.write(Unpooled.copiedBuffer(serialNumber.getBytes()));
                    ctx.write(Unpooled.copiedBuffer(datetime.getBytes()));
                    ctx.write(Unpooled.copiedBuffer(new byte[]{ServerToDevice}));
//                    ctx.write(Unpooled.copiedBuffer(intToByte(preInstallFlaginstallLength)));
                    ctx.write(Unpooled.copiedBuffer(intToByte(171)));

                    ctx.write(Unpooled.copiedBuffer(new byte[]{1, 1, 1, 1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1, 1, 1, 1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1, 1, 1, 1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1, 1, 1, 1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1, 1, 1, 1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1, 1, 1, 1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1}));  //4
                    ctx.write(Unpooled.copiedBuffer(new byte[]{1}));  //4

//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getRecordTime1(), Charset.defaultCharset()));  //4
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getRecordTime2(), Charset.defaultCharset()));  //4
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getRecordTime3(), Charset.defaultCharset()));  //4
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getFmFrequency(), Charset.defaultCharset()));    //6 x (10)
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getSid(), Charset.defaultCharset()));    //8 x (8)
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getPname(), Charset.defaultCharset()));  //10
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getPx(), Charset.defaultCharset())); //9 x(1)
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getPy(), Charset.defaultCharset())); //9 x(1)
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getSerialNumber(), Charset.defaultCharset())); //9 x(1)
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getPeriod(), Charset.defaultCharset())); //9 x(1)
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getSamplingTime(), Charset.defaultCharset())); //9 x(1)

//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getSamplerate(), Charset.defaultCharset())); //9 x(1)
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getServerUrl(), Charset.defaultCharset())); //9 x(1)
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getServerPort(), Charset.defaultCharset())); //9 x(1)
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getDbUrl(), Charset.defaultCharset())); //9 x(1)
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getDbPort(), Charset.defaultCharset())); //9 x(1)
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getRadioTime(), Charset.defaultCharset())); //9 x(1)
//                    ctx.write(Unpooled.copiedBuffer(preInstallResponseModel.getBaudrate(), Charset.defaultCharset())); //9 x(1)
//                    ctx.write(Unpooled.copiedBuffer(chkSumData));

                    StringBuilder a = new StringBuilder();
                    ctx.write(calcCheckSum.makeChecksum(a.append("1".repeat(171)).toString()));

                    ctx.flush();
                    mBuf.release();
                } else {
                    ctx.writeAndFlush(Unpooled.copiedBuffer(nak));
                    System.out.println("[CheckSum][FAIL] : Not Accurate");
                    mBuf.release();
                }

            } else if (flag.equals("8") || flag.equals("9")) {
                /*==== Header ====*/
                System.out.println("=== [PREINSTALL REPORT PROCESS RECEIVE START ] ===");
                String serialNum = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();
                String datetime = mBuf.readCharSequence(15, Charset.defaultCharset()).toString();
                String paraLen = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                /*==== Body ====*/
                String debugMessage = mBuf.readCharSequence(13, Charset.defaultCharset()).toString();
                String recordingTime1 = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String recordingTime2 = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String recordingTime3 = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String fmRadio = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String firmWareVersion = mBuf.readCharSequence(6, Charset.defaultCharset()).toString();
                String batteryVtg = mBuf.readCharSequence(6, Charset.defaultCharset()).toString();
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
                String radioTime = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String baudrate = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();//Number
                String baudrateNext = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();//Number
                String pcbVersion = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                byte chkSum1 = (mBuf.readByte());
                byte chkSum2 = (mBuf.readByte());

                // 체크썸 기능 빠짐
                preinstallReportModel.setSerialNumber(serialNum);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
                LocalDateTime dateTime = LocalDateTime.parse(datetime, formatter);
                preinstallReportModel.setDateTime(dateTime);
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

                boolean reportResult = preinstallSensorListService.insertReport(preinstallReportModel);

                if (reportResult) {  // 체크썸 값이 맞다면 buff에 write
                    byte ack = 8;
                    ctx.write(Unpooled.copiedBuffer(new byte[]{ack}));
                    ctx.flush();
                    mBuf.release();
                    log.info("Report inserted");
                } else {
                    byte nak = 9;
                    ctx.write(Unpooled.copiedBuffer(new byte[]{nak}));
                    ctx.flush();
                    mBuf.release();
                    log.error("Report insert failed");
                }
            } else if (flag.equals("6")) {
                String serialNumber = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();
                String datetime = mBuf.readCharSequence(15, Charset.defaultCharset()).toString();
                String requestType = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String paraLen = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String sid = mBuf.readCharSequence(16, Charset.defaultCharset()).toString();
                String pname = mBuf.readCharSequence(16, Charset.defaultCharset()).toString();
                byte chksum1 = (mBuf.readByte());
                byte chksum2 = (mBuf.readByte());

                String convertChk = String.format("%x%x", chksum1, chksum2);
                String chkData = flag + serialNumber + datetime + requestType + paraLen + sid + pname;

                int convertDecimalSum = 0;

                for (int i = 0; i < chkData.length(); i++) {
                    convertDecimalSum += chkData.charAt(i);    // 문자열 10진수로 바꿔서 저장
                }
                System.out.println("[flag] " + flag);
                System.out.println("[serialNumber] " + serialNumber);
                System.out.println("[datetime] " + datetime);
                System.out.println("[requestType] " + requestType);
                System.out.println("[paraLen] " + paraLen);
                System.out.println("[sid] " + sid);
                System.out.println("[pname] " + pname);
                System.out.println("[chksum1] " + chksum1);
                System.out.println("[chksum2] " + chksum2);
                System.out.println("[convertChk] " + convertChk);
                int decimal = Integer.parseInt(convertChk, 16);
                System.out.println("[decimal] " + decimal);
                System.out.println("[convertDecimalSum] " + convertDecimalSum);
                System.out.println("=====================");

                if (convertDecimalSum == decimal) {
                    System.out.println("[CheckSum] : SUCCESS :)");
                    settingDeviceInfos = settingSensorListService.settingFindData(flag, serialNumber);
                    System.out.println("[SettingDeviceInfos] : " + settingDeviceInfos.toString());
                }

                if (settingDeviceInfos != null) {
                    ctx.write(Unpooled.copiedBuffer(ack));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getTime1().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getTime2().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getTime3().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getFmFrequency().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getSid().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getPname().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getSleep().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getReset().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getPeriod().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getSamplingTime().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getFReset().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getPx().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getPy().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getActive().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getActive().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getSampleRate().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getServerUrl().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getServerPort().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getDbUrl().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getDbPort().getBytes()));
                    ctx.write(Unpooled.copiedBuffer(settingDeviceInfos.getRadioTime().getBytes()));

                    ctx.flush();
                    mBuf.release();
                } else {
                    ctx.writeAndFlush(Unpooled.copiedBuffer(nak));
                    System.out.println("[CheckSum][FAIL] : Not Accurate");
                    mBuf.release();
                }
            } else if (flag.equals("7")) {
                String serialNumber = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();
                String requestType = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String paraLen = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String endRecordingTime = mBuf.readCharSequence(13, Charset.defaultCharset()).toString();
                String recordingTime1 = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String recordingTime2 = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String recordingTime3 = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String fmRadio = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String firmWareVersion = mBuf.readCharSequence(6, Charset.defaultCharset()).toString();
                String batteryVtg = mBuf.readCharSequence(6, Charset.defaultCharset()).toString();
                String RSSI = mBuf.readCharSequence(3, Charset.defaultCharset()).toString(); //Number
                String deviceStatus = mBuf.readCharSequence(2, Charset.defaultCharset()).toString();
                String samplingTime = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();//Number
                String px = mBuf.readCharSequence(10, Charset.defaultCharset()).toString();
                String py = mBuf.readCharSequence(10, Charset.defaultCharset()).toString();
                String modemNumber = mBuf.readCharSequence(16, Charset.defaultCharset()).toString();
                String sid = mBuf.readCharSequence(16, Charset.defaultCharset()).toString(); //*
                String period = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String serverUrl = mBuf.readCharSequence(32, Charset.defaultCharset()).toString(); //*
                String serverPort = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String DBUrl = mBuf.readCharSequence(32, Charset.defaultCharset()).toString(); //*
                String DBPort = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String sleep = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String active = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String fReset = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String reset = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String samplerate = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String radioTime = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String cregCnt = mBuf.readCharSequence(3, Charset.defaultCharset()).toString();
                String sleepCnt = mBuf.readCharSequence(3, Charset.defaultCharset()).toString();
                byte chksum1 = (mBuf.readByte());
                byte chksum2 = (mBuf.readByte());

                String convertChk = String.format("%x%x", chksum1, chksum2);
                String chkData = flag + serialNumber + endRecordingTime + requestType + paraLen + sid + recordingTime1 + recordingTime2 + recordingTime3 +
                        fmRadio + firmWareVersion + batteryVtg + RSSI + deviceStatus + samplingTime + px + py + modemNumber + period + serverUrl + serverPort +
                        DBUrl + DBPort + sleep + active + fReset + reset + samplerate + radioTime + cregCnt + sleepCnt;

                int convertDecimalSum = 0;

                for (int i = 0; i < chkData.length(); i++) {
                    convertDecimalSum += chkData.charAt(i);    // 문자열 10진수로 바꿔서 저장
                }

                System.out.println("===========data process (D-->S)==========");
                System.out.println("[convertChk] " + convertChk);
                int decimal = Integer.parseInt(convertChk, 16);
                System.out.println("[decimal] " + decimal);
                System.out.println("[convertDecimalSum] " + convertDecimalSum);
                System.out.println("=================================");

                if (convertDecimalSum == decimal) {
                    System.out.println("[CheckSum] : SUCCESS :)");
                    dataInsertModel.setEndRecordTime(endRecordingTime.trim());
                    dataInsertModel.setTime1(recordingTime1.trim());
                    dataInsertModel.setTime2(recordingTime2.trim());
                    dataInsertModel.setTime3(recordingTime3.trim());
                    dataInsertModel.setFmFrequency(fmRadio.trim());
                    dataInsertModel.setFirmwareVersion(firmWareVersion.trim());
                    dataInsertModel.setBatteryVtg(batteryVtg.trim());
                    dataInsertModel.setRSSI(RSSI.trim());
                    dataInsertModel.setDeviceStatus(deviceStatus.trim());
                    dataInsertModel.setSamplingTime(samplingTime.trim());
                    dataInsertModel.setPx(px.trim());
                    dataInsertModel.setPy(py.trim());
                    dataInsertModel.setModemNumber(modemNumber.trim());
                    dataInsertModel.setSid(sid.trim());
                    dataInsertModel.setPeriod(period.trim());
                    dataInsertModel.setServerUrl(serverUrl.trim());
                    dataInsertModel.setServerPort(serverPort.trim());
                    dataInsertModel.setDbUrl(DBUrl.trim());
                    dataInsertModel.setDbPort(DBPort.trim());
                    dataInsertModel.setSleep(sleep.trim());
                    dataInsertModel.setActive(active.trim());
                    dataInsertModel.setFReset(fReset.trim());
                    dataInsertModel.setReset(reset.trim());
                    dataInsertModel.setSampleRate(samplerate.trim());
                    dataInsertModel.setRadioTime(radioTime.strip());

                    reportFindResults = dataSensorListService.findDataExistence(flag, serialNumber);

                    if (Objects.isNull(reportFindResults)) {
                        System.out.println("[fail] : 값이 존재하질 않습니다");
                    } else {
                        System.out.println("[reportFindResults]" + reportFindResults.toString());
                        // 펌웨어 받은 값을 sensor_report_(sid)_(sn) 에 INSERT
                        if (dataSensorListService.insertUniqueInformation(dataInsertModel, reportFindResults.getAsid(), reportFindResults.getAproject(), reportFindResults.getSsn())) {
                            ctx.writeAndFlush(Unpooled.copiedBuffer(ack));
                            mBuf.release();
                        } else {
                            ctx.writeAndFlush(Unpooled.copiedBuffer(nak));
                            mBuf.release();
                        }
                    }
                }
            } else if (flag.equals("5")) {
                String serialNumber = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();
                String datetime = mBuf.readCharSequence(15, Charset.defaultCharset()).toString();
                String requestType = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String paraLen = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();
                String data = mBuf.readCharSequence(256, Charset.defaultCharset()).toString();
                byte chksum1 = (mBuf.readByte());
                byte chksum2 = (mBuf.readByte());


                log.info(serialNumber);
                log.info(datetime);
                log.info(requestType);
                log.info(paraLen);
                log.info(data);

                String convertChk = String.format("%x%x", chksum1, chksum2);
                String chkData = flag + serialNumber + datetime + requestType + paraLen + data;

                int convertDecimalSum = 0;

                for (int i = 0; i < chkData.length(); i++) {
                    convertDecimalSum += chkData.charAt(i);    // 문자열 10진수로 바꿔서 저장
                }

                int decimal = Integer.parseInt(convertChk, 16);
                System.out.println("[decimal] " + decimal);
                System.out.println("[convertDecimalSum] " + convertDecimalSum);
                System.out.println("=====================");

                log.info(dataRefModel.getFilepath());

                File file = new File(dataRefModel.getFilepath());
                FileWriter writer = null;

                if (convertDecimalSum == decimal) {
                    System.out.println("[CheckSum] : SUCCESS :)");

                    for (int i = 0; i < framesize; i++) {
                        writer = new FileWriter(file, true);
                        writer.write(data);
                        writer.flush();
                    }


                }
            } else if (flag.equals("4")) {
                String serialNumber = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();
                String datetime = mBuf.readCharSequence(15, Charset.defaultCharset()).toString();
                String requestType = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String paraLen = mBuf.readCharSequence(4, Charset.defaultCharset()).toString();

                String frame = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                String dataSize = mBuf.readCharSequence(3, Charset.defaultCharset()).toString();
                String sampleRate = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
                byte chksum1 = (mBuf.readByte());
                byte chksum2 = (mBuf.readByte());

                // data process에서 쓰고자  static으로 선언.
                framesize = Integer.parseInt(frame);

                String convertChk = String.format("%x%x", chksum1, chksum2);
                String chkData = flag + serialNumber + datetime + requestType + paraLen + frame + dataSize + sampleRate;

                int convertDecimalSum = 0;

                for (int i = 0; i < chkData.length(); i++) {
                    convertDecimalSum += chkData.charAt(i);    // 문자열 10진수로 바꿔서 저장
                }

                int decimal = Integer.parseInt(convertChk, 16);
                System.out.println("[decimal] " + decimal);
                System.out.println("[convertDecimalSum] " + convertDecimalSum);
                System.out.println("=====================");

                if (convertDecimalSum == decimal) {
                    System.out.println("[CheckSum] : SUCCESS :)");

                    requestFindResults = requestSensorListService.findDataExistence(flag, serialNumber);

                    if (requestFindResults == null) {
                        System.out.println("[fail] : SENSOR_LIST_ALL 테이블에 값이 존재하질 않습니다");
                    } else {
                        String path1 = "C:\\dev\\" + requestFindResults.getAsid();
                        String path2 = "C:\\dev\\" + requestFindResults.getAsid() + "\\" + requestFindResults.getAproject();
                        String path3 = "C:\\dev\\" + requestFindResults.getAsid() + "\\" + requestFindResults.getAproject() + "\\" + requestFindResults.getSsn();

                        String convertedSampleRate;

                        /*
                           sampleRate 기존 4,8  외에 16 일 경우, 변환
                           sampleRate = 한자리 --> 004 or 008
                           sampleRate = 두자리 --> 016
                        */

                        if (sampleRate.length() == 1) {
                            convertedSampleRate = "00" + sampleRate;
                            log.info(convertedSampleRate);
                        } else {
                            convertedSampleRate = "0" + sampleRate;
                            log.info(convertedSampleRate);
                        }

                        char underBar = '_';
                        String filePath = path3 + "\\" + serialNumber + underBar + datetime + underBar + convertedSampleRate + ".dat";
                        File initFilePath = new File(filePath);
                        File file1 = new File(path1);
                        File file2 = new File(path2);
                        File file3 = new File(path3);
                        Path filePathExistence = Paths.get(filePath);

                        dataRefModel.setFilepath(filePath);
                        log.info(dataRefModel.getFilepath());


                        if (file1.isDirectory()) {
                            System.out.println("[PASS] : " + path1 + " 경로가 존재합니다");
                            if (file2.isDirectory()) {
                                System.out.println("[PASS] : " + path2 + " 경로가 존재합니다");
                                if (file3.isDirectory()) {
                                    System.out.println("[PASS] : " + path3 + " 경로가 존재합니다");

                                    if (Files.exists(filePathExistence)) {
                                        System.out.println("[EXIST] : " + filePath + " 경로에 파일이 존재합니다");

                                        // 해당경로 파일 존재 시, NAK 처리
                                        ctx.writeAndFlush(Unpooled.copiedBuffer(nak));
                                        mBuf.release();

                                    } else {
                                        System.out.println("[NOT EXIST] : " + filePath + " 경로에 파일이 존재하지 않습니다. 파일을 생성합니다.");
                                        // 해당경로 파일 없을 경우, ACK 처리
                                        ctx.writeAndFlush(Unpooled.copiedBuffer(ack));

                                        mBuf.release();

                                        try {
                                            if (initFilePath.createNewFile()) {
                                                System.out.println("File created");

                                            } else {
                                                System.out.println("File already exists");
                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    System.out.println("[FAIL] : " + path3 + " 경로가 존재하지 않습니다.");
                                }
                            } else {
                                System.out.println("[FAIL] : " + path2 + " 경로가 존재하지 않습니다.");
                            }
                        } else {
                            System.out.println("[FAIL] : " + path1 + " 경로가 존재하지 않습니다.");
                        }
                    }
                }
            }
        } catch (Exception e) {
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
