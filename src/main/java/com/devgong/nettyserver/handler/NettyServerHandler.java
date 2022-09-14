package com.devgong.nettyserver.handler;

import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.protocol.NakPacket;
import com.devgong.nettyserver.protocol.Packet;
import com.devgong.nettyserver.protocol.PacketFlag;
import com.devgong.nettyserver.protocol.RequestType;
import com.devgong.nettyserver.protocol.preinstall.PreInstallRequest;
import com.devgong.nettyserver.protocol.preinstall.PreInstallResponse;
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
import java.util.Arrays;
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

    final byte[] ack = {8};
    final byte[] nak = {9};

    DataRefModel dataRefModel = new DataRefModel();
    static int framesize;

//    public byte[] intToByte(int value) {
//        byte[] reValue;
//        reValue = new byte[4];
//
//        reValue[3] = (byte) (value);
//        reValue[2] = (byte) (value >> 8);
//        reValue[1] = (byte) (value >> 16);
//        reValue[0] = (byte) (value >> 24);
//        return reValue;
//    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf mBuf = (ByteBuf) msg;

        System.out.println("Channel Read");
        System.out.println("===================");

//        String flag = mBuf.readCharSequence(1, Charset.defaultCharset()).toString();
        byte readFlag = mBuf.readByte();
        PacketFlag flag = Arrays.stream(PacketFlag.values()).filter(f -> f.getFlag() == readFlag).findAny()
                .orElseThrow(() -> new IllegalStateException("Invalid flag error : " + readFlag));

        PreInstallSetModel preInstallDeviceInfos = null;
        SettingSetModel settingDeviceInfos = null;
        PreinstallReportModel preinstallReportModel = new PreinstallReportModel();

        DataInsertModel dataInsertModel = new DataInsertModel();
        PreInstallSensorListAllModel reportFindResults = new PreInstallSensorListAllModel();
        RequestListAllModel requestFindResults;

        /* 플래그에 값에 따라 분기*/
        /*  <<< Pre-Install Step >>> ===========================================================================================*/
        try {

            if (PacketFlag.PREINSTALL.equals(flag)) {

                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);
                log.info("readable bytes length : {}", bytes.length);


                log.info("FLAG : {}", flag);
                Packet<PreInstallRequest> request = new Packet<>(flag, bytes, PreInstallRequest.class);
                preInstallDeviceInfos = preinstallSensorListService.preInstallfindData(request.getParameter().getModemPhoneNumber());
                PreInstallResponse response = new PreInstallResponse(
                        preInstallDeviceInfos.getTime1(),
                        preInstallDeviceInfos.getTime2(),
                        preInstallDeviceInfos.getTime3(),
                        preInstallDeviceInfos.getFmFrequency(),
                        preInstallDeviceInfos.getSid(),
                        preInstallDeviceInfos.getPname(),
                        preInstallDeviceInfos.getPx(),
                        preInstallDeviceInfos.getPy(),
                        preInstallDeviceInfos.getSerialNumber(),
                        Integer.parseInt(preInstallDeviceInfos.getPeriod()),
                        Integer.parseInt(preInstallDeviceInfos.getSamplingTime()),
                        Integer.parseInt(preInstallDeviceInfos.getSampleRate()),
                        preInstallDeviceInfos.getServerUrl(),
                        preInstallDeviceInfos.getServerPort(),
                        preInstallDeviceInfos.getDbUrl(),
                        preInstallDeviceInfos.getDbPort(),
                        Integer.parseInt(preInstallDeviceInfos.getRadioTime()),
                        Integer.parseInt(preInstallDeviceInfos.getBaudrate())
                );

                if (preInstallDeviceInfos != null) {
                    Packet<PreInstallResponse> responsePacket = new Packet<>(
                            PacketFlag.PREINSTALL,
                            response.getSid(),
                            LocalDateTime.now(),
                            RequestType.SERVER,
                            response.serialize().length + 2,
                            response
                    );

                    for (byte a : responsePacket.serialize()) {
                        log.info("responsePacket : {}", a);
                        log.info("responsePacket(char) : {}", (char) a);
                    }

                    ctx.write(responsePacket.serialize());
                    ctx.flush();
                    mBuf.release();
                } else {
                    ctx.writeAndFlush(new NakPacket("0".repeat(24), LocalDateTime.now()).serialize());
                    System.out.println("[CheckSum][FAIL] : Not Accurate");
                    mBuf.release();
                }

            } else if (PacketFlag.ACK.equals(flag) || PacketFlag.NAK.equals(flag)) {
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
                    settingDeviceInfos = settingSensorListService.settingFindData(flag.getFlag() + "", serialNumber);
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

                    reportFindResults = dataSensorListService.findDataExistence(flag.getFlag() + "", serialNumber);

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

                    requestFindResults = requestSensorListService.findDataExistence(flag.getFlag() + "", serialNumber);

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
