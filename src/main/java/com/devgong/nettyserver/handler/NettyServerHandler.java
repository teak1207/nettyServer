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
    private final DataService dataService;

    DataInsertModel dataInsertModel = new DataInsertModel();
    PreInstallSensorListAllModel findResult = new PreInstallSensorListAllModel();
    RequestListAllModel requestFindResults;
    RequestListAllModel dataFindResults;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //seq : 장치에서 전송해주는 바이트를 byteBuf 타입으로 형변환하여 초기화
        ByteBuf mBuf = (ByteBuf) msg;

        log.info("Channel Read");
        log.info("===================");
        //seq : 프로토콜의 첫 바이트는 flag value 이기에, readFlag 값 할당
        byte readFlag = mBuf.readByte();

        //seq : 만약 flag value 가 PacketFlag에 정의해놓은 값이 아닌 경우, 예외처리 (IllegalStateException)
        PacketFlag flag = Arrays.stream(PacketFlag.values()).filter(f -> f.getFlag() == readFlag).findAny()
                .orElseThrow(() -> new IllegalStateException("Invalid flag error : " + readFlag));

        //memo : preinstall response 담을 객체 생성
        PreInstallSetModel preInstallDeviceInfos;
        //memo : setting response 담을 객체 생성
        SettingResponseModel settingDeviceInfos;


        //seq : flag 값에 따른 분기 처리
        try {
            //seq : preinstall value (A) 인 경우 분기
            if (PacketFlag.PREINSTALL.equals(flag)) {

                //seq : mBuf 에서 읽을수 있는 바이트수를 반환해서 byte[] 에 담음. readableBytes()는 netty에서 사용되는 메서드
                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);  // bytes 의 내용을 mBuf 에 담음.

                log.info("PreInstall  readable bytes length : {}", bytes.length);
                log.info("PreInstall FLAG : {}", (char) readFlag);

                //seq : <<장치에서 보낸 값과 서버에서 받은 값이 타당한지를 수행하는 과정 ,바이트배열->객체 >>
                //seq : packet 이라는 클래스를 해두었음. 생성자의 파라미터로  flag, bytes, PreInstallRequest.class 줌.
                //seq : 프로토콜헤더 항목 길이별로 맞게 할당하는 처리수행.
                //seq : 헤더에서 넘어오는 parameterLength 체크하는 작업 + checkSum 타당성검사 작업 수행
                Packet<PreInstallRequest> request = new Packet<>(flag, bytes, PreInstallRequest.class);

                //seq : <<preinstall 프로세스 진행>>
                preInstallDeviceInfos = preinstallSensorListService.preInstallFindData(request.getParameter().getModemPhoneNumber());

                //seq : preinstallSensorListService.preInstallFindData 에서 가져온 값을 장치로 보내주기위한 작업 진행.
                //seq : preinstall,Server->Device
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


                //seq : preInstallDeviceInfos null 체크 후, 객체-> 바이트배열 변환
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
                    log.info("[CheckSum][FAIL] : Not Accurate");

                    mBuf.release();
                }
                //seq : 장치에서 보낸 ACK or NAK + REPORT 값을 받는 과정
            } else if (PacketFlag.ACK.equals(flag) || PacketFlag.NAK.equals(flag)) {
                /*==== Header ====*/
                log.info("=== [PREINSTALL REPORT PROCESS RECEIVE START ] ===");
                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);

                log.info("ACK/NAK  Readable bytes length : {}", bytes.length);
                log.info("ACK/NAK FLAG : {}", (char) readFlag);

                Packet<PreInstallReportRequest> request = new Packet<>(flag, bytes, PreInstallReportRequest.class);

                boolean reportResult = preinstallSensorListService.insertReport(bytes);

                byte[] result = new byte[45];


                // seq : reportResult 의 값에 따른 분기
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
                //seq : SETTING value (6) 인 경우 분기
            } else if (PacketFlag.SETTING.equals(flag)) {

                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);  // bytes 의 내용을 mBuf 에 담음.

                Packet<SettingRequest> request = new Packet<>(flag, bytes, SettingRequest.class);

                log.info("Setting Readable bytes length : {}", bytes.length);
                log.info("setting request check : {}", request);
                //seq : << Setting process 진행 >>
                settingDeviceInfos = settingSensorListService.settingRequestData(request.getSensorId());
                log.info("settingDeviceInfos Check : {}", settingDeviceInfos);


                byte[] nakResponse = new byte[45];

                //seq : 리턴받은 값을 settingDeviceInfos 객체에 채워넣음.
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
                    //seq : settingDeviceInfos 객체 -> 바이트 배열로 변환
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


                    //seq : Nak 인 경우, byte[45] 의 첫 index에 NAK(9) 만 담아서 보냄.
                } else {
                    nakResponse[0] = PacketFlag.NAK.getFlag();
                    ctx.write(Unpooled.copiedBuffer(nakResponse));
                    ctx.flush();
                    mBuf.release();
                }

                //seq : << Report 프로세스 진행 >>
            } else if (PacketFlag.REPORT.equals(flag)) {

                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);

                Packet<ReportRequest> request = new Packet<>(flag, bytes, ReportRequest.class);
                String serialNumber = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();
                //seq : serialNumber 으로 sensor_list_all 에서 존재유무 후, findResult 담음
                findResult = reportSensorListService.findDataExistence(serialNumber);

                byte[] response = new byte[45];

                if (Objects.isNull(findResult)) {
                    log.info("[FAIL] : 값이 존재하질 않습니다");

                } else {
                    log.info("reportFindResults:{}", findResult);

                    //memo : sid 공유변수인데 data process에서 쓰려고 만듦.
//                    sid = request.getParameter().getSid();
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
                log.info("test length: {}", bytes.length);

                NewPacket<ReqRequest> request = new NewPacket<>(flag, bytes, ReqRequest.class);
                log.info("Setting Readable bytes length : {}", bytes.length);
                log.info("settingResponset check : {}", request);

                byte[] response = new byte[45];

                requestFindResults = requestSensorListService.findDataExistence(request.getSensorId());

                //memo : leak_send_data 에  저장
                requestSensorListService.saveData(request, requestFindResults);


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

                log.info("Data  길이 : {}", bytes.length);
                log.info("Data FLAG : {}", (char) readFlag);
                log.info("mBuf length : {}", mBuf);

                //memo : request에 참조 없음-> sensor_list_all에서 참조해옴.
                //memo : sensor_list_all에서 가져온값으로 leak_send_data_(sid)_(sn) 테이블명 변수 만듦.
                dataFindResults = requestSensorListService.findDataExistence(request.getSensorId(), "-1", "0");
                log.info("dataFindResults : {}", dataFindResults);

                //memo : leak_send_data_(sid)_(sn)에서 fname 참조해야함.
                String fname =requestSensorListService.findDataFname(dataFindResults.getSsn(), dataFindResults.getAsid());

                log.info("fname : {}",fname);
                //memo : dat file (frame amount * Data*size)에 저장.
                dataService.saveData(request);

                //memo : 정상적으로 저장 후, send_data 의 complete, complete_time UPDATE 진행.
//                dataService.updateData();


                response[0] = PacketFlag.ACK.getFlag();
                ctx.write(Unpooled.copiedBuffer(response));
                ctx.flush();
                mBuf.release();
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
