package com.devgong.nettyserver.handler;

import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.protocol.NakPacket;
import com.devgong.nettyserver.protocol.Packet;
import com.devgong.nettyserver.protocol.PacketFlag;
import com.devgong.nettyserver.protocol.Report.ReportRequest;
import com.devgong.nettyserver.protocol.RequestType;
import com.devgong.nettyserver.protocol.preinstall.PreInstallReportRequest;
import com.devgong.nettyserver.protocol.preinstall.PreInstallRequest;
import com.devgong.nettyserver.protocol.preinstall.PreInstallResponse;
import com.devgong.nettyserver.protocol.setting.SettingRequest;
import com.devgong.nettyserver.protocol.setting.SettingResponse;
import com.devgong.nettyserver.service.ReportSensorListService;
import com.devgong.nettyserver.service.PreinstallSensorListService;
import com.devgong.nettyserver.service.RequestSensorListService;
import com.devgong.nettyserver.service.SettingSensorListService;
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
    private final ReportSensorListService reportSensorListService;
    private final RequestSensorListService requestSensorListService;

    final byte[] ack = {8};
    final byte[] nak = {9};

    DataRefModel dataRefModel = new DataRefModel();
    DataInsertModel dataInsertModel = new DataInsertModel();
    PreInstallSensorListAllModel findResult = new PreInstallSensorListAllModel();
    RequestListAllModel requestFindResults;

    static int framesize;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf mBuf = (ByteBuf) msg;

        System.out.println("Channel Read");
        System.out.println("===================");

        byte readFlag = mBuf.readByte();
        PacketFlag flag = Arrays.stream(PacketFlag.values()).filter(f -> f.getFlag() == readFlag).findAny()
                .orElseThrow(() -> new IllegalStateException("Invalid flag error : " + readFlag));

        //memo : preinstall response 담을 객체 생성
        PreInstallSetModel preInstallDeviceInfos;
        //memo : setting response 담을 객체 생성
        SettingResponseModel settingDeviceInfos;




        /* 플래그에 값에 따라 분기*/
        /*  <<< Pre-Install  >>> ===========================================================================================*/
        try {

            if (PacketFlag.PREINSTALL.equals(flag)) {

                byte[] bytes = new byte[mBuf.readableBytes()];

                mBuf.duplicate().readBytes(bytes);  // bytes 의 내용을 mBuf 에 담음.

                log.info("PreInstall  readable bytes length : {}", bytes.length);
                log.info("PreInstall FLAG : {}", (char) readFlag);

//                for (int i = 0; i < bytes.length; i++) {
//                    log.info("bytes : {}", (char) bytes[i]);
//                }

                Packet<PreInstallRequest> request = new Packet<>(flag, bytes, PreInstallRequest.class);
                preInstallDeviceInfos = preinstallSensorListService.preInstallFindData(request.getParameter().getModemPhoneNumber());
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
                        Integer.parseInt((preInstallDeviceInfos.getBaudrate()).substring(0, 3))
                );

                if (preInstallDeviceInfos != null) {

                    log.info("response.serialize() : {}", response.serialize().length);
                    log.info("response.serialize().length + 2: {}", response.serialize().length + 2);
                    Packet<PreInstallResponse> responsePacket = new Packet<>(
                            PacketFlag.PREINSTALL,
                            response.getSn(),
                            LocalDateTime.now(),
                            RequestType.SERVER,
                            response.serialize().length + 2,  //4 byte
                            response
                    );
                    ctx.writeAndFlush(Unpooled.copiedBuffer(responsePacket.serialize()));
                    mBuf.release();

                } else {
                    ctx.writeAndFlush(new NakPacket("0".repeat(24), LocalDateTime.now()).serialize());
                    System.out.println("[CheckSum][FAIL] : Not Accurate");
                    mBuf.release();
                }
            } else if (PacketFlag.ACK.equals(flag) || PacketFlag.NAK.equals(flag)) {
                /*==== Header ====*/
                System.out.println("=== [PREINSTALL REPORT PROCESS RECEIVE START ] ===");
                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);

                log.info("ACK/NAK  Readable bytes length : {}", bytes.length);
                log.info("ACK/NAK FLAG : {}", (char) readFlag);

                Packet<PreInstallReportRequest> request = new Packet<>(flag, bytes, PreInstallReportRequest.class);

                boolean reportResult = preinstallSensorListService.insertReport(bytes);

                byte[] result = new byte[45];

                //TODO : Header 값이 아닌 flag+null 45byte 만 보내고 있음!!! 수정해야함.
                if (reportResult) {
                    result[0] = PacketFlag.ACK.getFlag();
                    ctx.write(Unpooled.copiedBuffer(result));
                    ctx.flush();
                    mBuf.release();
                    log.info("Report Response Success");

                } else {
                    result[0] = PacketFlag.NAK.getFlag();
                    ctx.write(Unpooled.copiedBuffer(result));
                    ctx.flush();
                    mBuf.release();
                    log.error("Report Insert Failed");
                }

            } else if (PacketFlag.SETTING.equals(flag)) {

                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);  // bytes 의 내용을 mBuf 에 담음.

                Packet<SettingRequest> request = new Packet<>(flag, bytes, SettingRequest.class);

                log.info("Setting Readable bytes length : {}", bytes.length);
                log.info("Setting Request check : {}", request);

                settingDeviceInfos = settingSensorListService.settingRequestData(request.getSensorId());
                log.info("settingDeviceInfos Check : {}", settingDeviceInfos);

                if (settingDeviceInfos != null) {
                    SettingResponse response = new SettingResponse(
                            settingDeviceInfos.getTime1(),
                            settingDeviceInfos.getTime2(),
                            settingDeviceInfos.getTime3(),
                            settingDeviceInfos.getFmRadio(),
                            settingDeviceInfos.getSid(),
                            settingDeviceInfos.getPname(),
                            settingDeviceInfos.getSleep(),
                            settingDeviceInfos.getReset(),
                            Integer.parseInt(settingDeviceInfos.getPeriod()),
                            Integer.parseInt(settingDeviceInfos.getSamplingTime()),
                            settingDeviceInfos.getFReset(),
                            settingDeviceInfos.getPx(),
                            settingDeviceInfos.getPy(),
                            settingDeviceInfos.getActive(),
                            Integer.parseInt(settingDeviceInfos.getSampleRate()),
                            settingDeviceInfos.getServerUrl(),
                            settingDeviceInfos.getServerPort(),
                            settingDeviceInfos.getDbUrl(),
                            settingDeviceInfos.getDbPort(),
                            Integer.parseInt(settingDeviceInfos.getRadioTime())
                    );

                    Packet<SettingResponse> responsePacket = new Packet<>(
                            PacketFlag.SETTING,
                            response.getSid(),
                            LocalDateTime.now(),
                            RequestType.SERVER,
                            response.serialize().length + 2,  //4 byte
                            response
                    );
                    ctx.writeAndFlush(Unpooled.copiedBuffer(responsePacket.serialize()));
                    mBuf.release();


                } else {

                    //memo : nak 45 byte 처리하기
                    ctx.writeAndFlush(Unpooled.copiedBuffer(nak));
                    log.info("setting response fail ");

                    mBuf.release();
                }


            } else if (PacketFlag.REPORT.equals(flag)) {

                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);
                log.info("Report Readable bytes length : {}", bytes.length);
                log.info("Report flag : {}", flag);

                Packet<ReportRequest> request = new Packet<>(flag, bytes, ReportRequest.class);

                String serialNumber = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();

                findResult = reportSensorListService.findDataExistence(serialNumber);


                if (Objects.isNull(findResult)) {
                    log.info("[FAIL] : 값이 존재하질 않습니다");

                } else {
                    System.out.println("[reportFindResults]" + findResult.toString());

                    // 펌웨어 받은 값을 sensor_report_(sid)_(sn) 에 INSERT
                    if (reportSensorListService.insertUniqueInformation(dataInsertModel, findResult.getAsid(), findResult.getAproject(), findResult.getSsn(),request)) {

                        log.info("야호");
//                        ctx.writeAndFlush(Unpooled.copiedBuffer(ack));
//                        mBuf.release();

                    } else {
                        log.info("문상후");
//                        ctx.writeAndFlush(Unpooled.copiedBuffer(nak));
//                        mBuf.release();
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
