package com.devgong.nettyserver.handler;

import com.devgong.nettyserver.domain.*;
import com.devgong.nettyserver.protocol.*;
import com.devgong.nettyserver.protocol.Report.ReportRequest;
import com.devgong.nettyserver.protocol.data.DataRequest;
import com.devgong.nettyserver.protocol.preinstall.PreInstallReportRequest;
import com.devgong.nettyserver.protocol.preinstall.PreInstallRequest;
import com.devgong.nettyserver.protocol.preinstall.PreInstallResponse;
import com.devgong.nettyserver.protocol.request.ReqRequest;
import com.devgong.nettyserver.protocol.setting.SettingRequest;
import com.devgong.nettyserver.protocol.setting.SettingResponse;
import com.devgong.nettyserver.service.*;
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
    private final DataService  dataService;

    DataInsertModel dataInsertModel = new DataInsertModel();
    PreInstallSensorListAllModel findResult = new PreInstallSensorListAllModel();
    RequestListAllModel requestFindResults;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf mBuf = (ByteBuf) msg;


        log.info("Channel Read");
        log.info("===================");

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

                byte[] nakResponse = new byte[45];
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
                    nakResponse[0] = PacketFlag.NAK.getFlag();
                    ctx.write(Unpooled.copiedBuffer(nakResponse));
                    ctx.flush();
                    mBuf.release();
                }


            } else if (PacketFlag.REPORT.equals(flag)) {

                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);

                Packet<ReportRequest> request = new Packet<>(flag, bytes, ReportRequest.class);
                String serialNumber = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();
                findResult = reportSensorListService.findDataExistence(serialNumber);

                byte[] response = new byte[45];

                if (Objects.isNull(findResult)) {
                    log.info("[FAIL] : 값이 존재하질 않습니다");

                } else {
                    log.info("reportFindResults:{}", findResult);

                    // 펌웨어 받은 값을 sensor_report_(sid)_(sn) 에 INSERT
                    if (reportSensorListService.insertUniqueInformation(dataInsertModel, findResult.getAsid(), findResult.getAproject(), findResult.getSsn(), request)) {
                        response[0] = PacketFlag.ACK.getFlag();
                        ctx.write(Unpooled.copiedBuffer(response));
                        ctx.flush();
                        mBuf.release();
                        log.info("REPORT Process Success");

                    } else {
                        response[0] = PacketFlag.NAK.getFlag();
                        ctx.write(Unpooled.copiedBuffer(response));
                        ctx.flush();
                        mBuf.release();
                        log.info("REPORT Process fail");
                    }
                }
            } else if (PacketFlag.REQUEST.equals(flag)) {

                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);

                log.info("flag : {}", flag);
                for (byte a : bytes) {
                    log.info("test : {}", (char) a);
                    log.info("test : {}", a);
                    log.info("----------");
                }
                log.info("test length: {}", bytes.length);

                NewPacket<ReqRequest> request = new NewPacket<>(flag, bytes, ReqRequest.class);
                log.info("Setting Readable bytes length : {}", bytes.length);
                log.info("Setting Request check : {}", request);

                byte[] response = new byte[45];

                requestFindResults = requestSensorListService.findDataExistence(request.getSensorId());

                if (requestFindResults == null) {
                    log.info("[FAIL] : SENSOR_LIST_ALL 테이블에 값이 존재하지 않습니다.");
                } else {
                    if (requestSensorListService.confirmPath(requestFindResults, request)) {
                        response[0] = PacketFlag.ACK.getFlag();
                        ctx.write(Unpooled.copiedBuffer(response));
                        ctx.flush();
                        mBuf.release();

                    } else {
                        response[0] = PacketFlag.NAK.getFlag();
                        ctx.write(Unpooled.copiedBuffer(response));
                        ctx.flush();
                        mBuf.release();
                    }
                }


            } else if (PacketFlag.DATA.equals(flag)) {

                log.info("flag : {}", flag);
                byte[] response = new byte[45];
                byte[] bytes = new byte[mBuf.readableBytes()];

                mBuf.duplicate().readBytes(bytes);  // bytes 의 내용을 mBuf 에 담음.

                NewPacket<DataRequest> request = new NewPacket<>(flag, bytes, DataRequest.class);

                log.info("Data  readable bytes length : {}", bytes.length);
                log.info("Data FLAG : {}", (char) readFlag);
                log.info("mBuf length : {}", mBuf);

                dataService.saveData(request);









/*
                response[0] = PacketFlag.ACK.getFlag();
                ctx.write(Unpooled.copiedBuffer(response));
                ctx.flush();
                mBuf.release();
*/


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
