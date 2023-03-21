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
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * develop Spec
 * (추가 기입 해야함.)
 *
 * </p>
 *
 * @author devgong
 * 디바이스와의 통신관련 통제는 핸들러에서 이루어진다
 * (1) Preinstall - 디바이스에 기기 설정 값에 대한 정보를 서버에서 내려주는 프로세스
 * (2) Setting -
 * (3) Report -
 * (4) Request -
 * (5) Data - fnum의 값만큼 디바이스에서 값을 요청, 서버에서 응답
 * @version 1.0
 */


@Slf4j
@Component
@ChannelHandler.Sharable //#1 @Sharable 어노테이션은 여러채널에서 핸들러를 공유 할 수 있음을 나타냄.
@RequiredArgsConstructor
public class NettyServerHandler extends ChannelInboundHandlerAdapter {


    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final PreinstallSensorListService preinstallSensorListService;
    private final SettingPhaseService settingPhaseService;
    private final ReportSensorListService reportSensorListService;
    private final RequestSensorListService requestSensorListService;
    private final DataService dataService;
    private final DataSequenceService dataSequenceService;


    RequestListAllModel requestFindResults;
    RequestListAllModel dataFindResults;

    private final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);

/*
    public String byteArrayToHex(byte[] a) {

        StringBuilder sb = new StringBuilder();

        for (final byte b : a)

            sb.append(String.format("%02x ", b & 0xff));


        return sb.toString();
    }
*/


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // packet_info : 장치에서 전송해주는 바이트를 byteBuf 타입으로 형변환하여 초기화
        ByteBuf mBuf = (ByteBuf) msg;

        log.info("Channel Read");
        log.info("===================");
        // packet_info : 프로토콜의 첫 바이트는 flag value 이기에, readFlag 값 할당
        byte readFlag = mBuf.readByte();

        // packet_info : 만약 flag value 가 PacketFlag에 정의해놓은 값이 아닌 경우, 예외처리 (IllegalStateException)
        PacketFlag flag = Arrays.stream(PacketFlag.values()).filter(f -> f.getFlag() == readFlag).findAny()
                .orElseThrow(() -> new IllegalStateException("Invalid flag error : " + readFlag));

        // memo : preinstall response 담을 객체 생성
        PreInstallSetModel preInstallDeviceInfos;


        try {
            if (PacketFlag.PREINSTALL.equals(flag)) {

                double beforeTime = System.currentTimeMillis();

                // packet_info : mBuf 에서 읽을수 있는 바이트수를 반환해서 byte[] 에 담음. readableBytes()는 netty에서 사용되는 메서드
                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);  // bytes 의 내용을 mBuf 에 담음.

                /*
                 packet_info : 장치에서 보낸 값과 서버에서 받은 값이 타당한지를 수행하는 과정 , (바이트배열->객체)
                 packet_info : packet 이라는 클래스를 해두었음. 생성자의 파라미터로  flag, bytes, PreInstallRequest.class 전달.
                 packet_info : 프로토콜헤더 항목 길이별로 맞게 할당하는 처리 수행.
                 packet_info : 헤더에서 넘어오는 parameterLength 체크하는 작업 + checkSum 타당성검사 작업 수행
                */

                Packet<PreInstallRequest> request = new Packet<>(flag, bytes, PreInstallRequest.class);

                // preinstall_seq : <<preinstall 프로세스 진행>>
                // preinstall_seq : 고유값 전송(modemNumber)
                preInstallDeviceInfos = preinstallSensorListService.preInstallFindData(request.getParameter().getModemPhoneNumber());

                //seq : preinstallSensorListService.preInstallFindData 에서 가져온 값을 장치로 보내주기위한 작업 진행.

                // preinstall_seq : preinstall,Server->Device, PreInstall 값 전송 or NAK
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

//                    log .info("response.serialize() : {}", response.serialize().length);
//                    log.info("response.serialize().length + 2: {}", response.serialize().length + 2);

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

                double afterTime = System.currentTimeMillis();
                double secDiffTime = (afterTime - beforeTime) / 1000;
//                log.info("[PREINSTALL][TIME] secDiffTime : {}", secDiffTime);


                // preinstall_seq :ACK or NAK + REPORT 전송
            } else if (PacketFlag.ACK.equals(flag) || PacketFlag.NAK.equals(flag)) {

                double beforeTime = System.currentTimeMillis();

                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);

//                log.info("ACK/NAK  Readable bytes length : {}", bytes.length);
//                log.info("ACK/NAK FLAG : {}", (char) readFlag);
//                log.info("테스트, preinstall byteCheck : {}",byteArrayToHex(bytes));


                Packet<PreInstallReportRequest> request = new Packet<>(flag, bytes, PreInstallReportRequest.class);


                // preinstall_seq : 기기에서 보내준 값을 factory_report 테이블에 저장하는 작업.
                boolean reportResult = preinstallSensorListService.insertReport(bytes);


                byte[] result = new byte[45];


                // preinstall_seq : reportResult 의 값에 따른 분기 처리 true(ACK) / false(NAK)
                if (reportResult) {

                    //preinstall_seq : sensor_list_all 의 값을 find
                    PreInstallSensorListAllModel sensorListAllModel = preinstallSensorListService.FindDataBySerialNumber(request.getSensorId());

                    //preinstall_seq : sensor_list_all 의  fReset 값을 update
                    if (sensorListAllModel.getFreset().equals("1")) {
                        preinstallSensorListService.updateFactoryReset(sensorListAllModel);

                        double afterTime = System.currentTimeMillis();
                        double secDiffTime = (afterTime - beforeTime) / 1000;
//                        log.info("[PREINSTALL REPORT][TIME] secDiffTime : {}", secDiffTime);

                    }


                    result[0] = PacketFlag.ACK.getFlag();
                    ctx.write(Unpooled.copiedBuffer(result));
                    ctx.flush();
                    mBuf.release();
                    log.info("[REPORT][SUCCESS] Report Response Success");


                } else {
                    result[0] = PacketFlag.NAK.getFlag();
                    ctx.write(Unpooled.copiedBuffer(result));
                    ctx.flush();
                    mBuf.release();
                    log.error("Report Insert Failed");
                }


                // setting_seq : SETTING value (6) 인 경우 분기
            } else if (PacketFlag.SETTING.equals(flag)) {


                LOGGER.trace("trace");
                LOGGER.debug("debug");
                LOGGER.info("info");
                LOGGER.warn("warn");
                LOGGER.error("error");


                double beforeTime = System.currentTimeMillis();

                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);  // bytes 의 내용을 mBuf 에 담음.

                Packet<SettingRequest> request = new Packet<>(flag, bytes, SettingRequest.class);

                byte[] nakResponse = new byte[45];

                // setting_seq : << Setting process 진행 >>
                // setting_seq : Setting 값 response 하기 위해 model 초기화.

                // setting_seq : 미설정센서 -> 설정값이 없는거? --> 현재 개발 서버 버젼에서는 초기값이 설정이 안된다카던대.....

                boolean result = settingPhaseService.getCheckLiveOperation(request.getSensorId());

                if (!result) {
                    nakResponse[0] = PacketFlag.NAK.getFlag();
                    ctx.write(Unpooled.copiedBuffer(nakResponse));
                    ctx.flush();
                    mBuf.release();
                } else {


                    // danger :(아키텍쳐수정) SettingResponseModel & SettingResponse 가 하는일이 똑같음
                    // danger :(아키텍쳐수정) SettingResponse 에 구현된 deserialize 같은 메서드를 SettingResponseModel 로 옮기거나, 아니면 반대로 옮겨야 함
                    Optional<SettingResponseModel> settingDeviceInfos = settingPhaseService.getResponseData(request.getSensorId());


                    // setting_seq : 리턴받은 값을 settingDeviceInfos 객체에 채워넣음.

                    // setting_seq  : settingDeviceInfos 존재 && freset 값이 1이 아니면 if문 실행.
                    if (settingDeviceInfos.isPresent()) {
                        SettingResponseModel deviceInfo = settingDeviceInfos.get();

                        SettingResponse response = new SettingResponse(
                                deviceInfo.getTime1(),
                                deviceInfo.getTime2(),
                                deviceInfo.getTime3(),
                                deviceInfo.getFmRadio(),
                                deviceInfo.getSid(),
                                deviceInfo.getPname(),
                                deviceInfo.getSleep(),
                                deviceInfo.getReset(),
                                Integer.parseInt(deviceInfo.getPeriod()),
                                Integer.parseInt(deviceInfo.getSamplingTime()),
                                deviceInfo.getFReset(),
                                deviceInfo.getPx(),
                                deviceInfo.getPy(),
                                deviceInfo.getActive(),
                                Integer.parseInt(deviceInfo.getSampleRate()),
                                deviceInfo.getServerUrl(),
                                deviceInfo.getServerPort(),
                                deviceInfo.getDbUrl(),
                                deviceInfo.getDbPort(),
                                Integer.parseInt(deviceInfo.getRadioTime())
                        );

                        //setting_seq : settingDeviceInfos 객체 -> 바이트 배열로 변환
                        Packet<SettingResponse> responsePacket = new Packet<>(
                                PacketFlag.SETTING,
                                response.getSid(),
                                LocalDateTime.now(),
                                RequestType.SERVER,
                                response.serialize().length + 2,  //4 byte
                                response
                        );

//                    log.info("테스트2 : {}", responsePacket.getParameter().getFReset());
                        ctx.writeAndFlush(Unpooled.copiedBuffer(responsePacket.serialize()));
                        mBuf.release();

                        double afterTime = System.currentTimeMillis();
                        double secDiffTime = (afterTime - beforeTime) / 1000;
//                        log.info("[SETTING][TIME] secDiffTime : {}", secDiffTime);

                        //setting_seq : Nak 인 경우, byte[45] 의 첫 index NAK(9) 만 담아서 보냄.
                    } else {
                        nakResponse[0] = PacketFlag.NAK.getFlag();
                        ctx.write(Unpooled.copiedBuffer(nakResponse));
                        ctx.flush();
                        mBuf.release();
                    }
                }
                // report_seq : << Report 프로세스 진행 >>, 주기보고
            } else if (PacketFlag.REPORT.equals(flag)) {

                double beforeTime = System.currentTimeMillis();

                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);

                Packet<ReportRequest> request = new Packet<>(flag, bytes, ReportRequest.class);
//                String serialNumber = mBuf.readCharSequence(24, Charset.defaultCharset()).toString();
//                log.info("chk2 : {}", request.getParameterLength());
//                log.info("chk2 : {}", Long.toHexString(request.getParameterLength()));


                // report_seq : serialNumber 으로 sensor_list_all 에서 존재유무 후, findResult 담음
                Optional<PreInstallSensorListAllModel> reportFindResult = reportSensorListService.findDataExistence(request.getSensorId());

                byte[] response = new byte[45];

                if (Objects.isNull(reportFindResult)) {
                    log.info("[REPORT][FAIL] : 값이 존재하질 않습니다");
                } else {
//                    log.info("reportFindResults:{}", reportFindResult);

                    // report_seq : 펌웨어 받은 값을 sensor_report_(sid)_(sn) 에 INSERT
                    // report_seq : ACK or NAK
                    if (reportSensorListService.insertUniqueInformation(reportFindResult.get().getAsid(), reportFindResult.get().getAproject(), reportFindResult.get().getSsn(), request)) {
                        response[0] = PacketFlag.ACK.getFlag();
                        ctx.write(Unpooled.copiedBuffer(response));
                        ctx.flush();
                        mBuf.release();
                        log.info("[REPORT][PASS] Process Success");


                        double afterTime = System.currentTimeMillis();
                        double secDiffTime = (afterTime - beforeTime) / 1000;
//                        log.info("[REPORT][TIME] secDiffTime : {}", secDiffTime);


                    } else {
                        response[0] = PacketFlag.NAK.getFlag();
                        ctx.write(Unpooled.copiedBuffer(response));
                        ctx.flush();
                        mBuf.release();
                        log.info("[REPORT][FAIL] Process fail");
                    }
                }
                // request_seq : (device-> server) request
            } else if (PacketFlag.REQUEST.equals(flag)) {

                double beforeTime = System.currentTimeMillis();

                byte[] bytes = new byte[mBuf.readableBytes()];
                mBuf.duplicate().readBytes(bytes);

                NewPacket<ReqRequest> request = new NewPacket<>(flag, bytes, ReqRequest.class);
                byte[] frameCountArr = new byte[2];
                System.arraycopy(bytes, 44, frameCountArr, 0, 2);

                byte[] response = new byte[45];
                // request_seq : find 값을 객체에 초기화
                requestFindResults = requestSensorListService.findDataExistence(request.getSensorId());

                // request_seq : requestSensorListService.saveInitData()처리. Data 처리전에 메뉴판에 등록
                RequestLeakDataModel model = requestSensorListService.saveInitData(request, requestFindResults, frameCountArr);
                dataSequenceService.enrollDataSequence(model.getSn(), Integer.parseInt(model.getFnum()), LocalDateTime.now());

                if (requestFindResults == null) {
                    log.info("[REQUEST][FAIL] : SENSOR_LIST_ALL 테이블에 값이 존재하지 않습니다.");
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

//                double afterTime = System.currentTimeMillis();
//                double secDiffTime = (afterTime - beforeTime) / 1000;
//                log.info("[REQUEST][TIME] secDiffTime : {}", secDiffTime);


            } else if (PacketFlag.DATA.equals(flag)) {

                double beforeTime = System.currentTimeMillis();

                byte[] response = new byte[45];
                byte[] bytes = new byte[mBuf.readableBytes()];

                mBuf.duplicate().readBytes(bytes);  // bytes 의 내용을 mBuf 에 담음.

                NewPacket<DataRequest> request = new NewPacket<>(flag, bytes, DataRequest.class);
//                log.info("Data  길이 : {}", bytes.length);
//                log.info("Data FLAG : {}", (char) readFlag);

                // data_seq : request 참조 없음-> sensor_list_all 참조해옴.
                // data_seq : sensor_list_all 가져온값으로 leak_send_data_(sid)_(sn) 테이블명 변수 만듦.
                dataFindResults = requestSensorListService.findDataExistence(request.getSensorId(), "-1", "0");
//                log.info("dataFindResults : {}", dataFindResults);
                // data_seq : leak_send_data_(sid)_(sn)에서 fName 참조해야함.
//                String fname = requestSensorListService.findDataFname(dataFindResults.getSsn(), dataFindResults.getAsid());
                // data_seq : 테이블에서 fNum 가져와서 그걸로 카운트 횟수를 처리하자.
                dataService.saveData(request.getSensorId(), dataFindResults.getAsid(), request.getParameter().getData());

//                log.info("sidchk1 : {}  - {}", request.getSensorId(), dataFindResults.getAsid());

                // data_seq : 정상적으로 저장 후, send_data 의 complete, complete_time UPDATE 진행.
//                dataService.updateData(fname, dataFindResults.getAsid(), dataFindResults.getSsn());

                response[0] = PacketFlag.ACK.getFlag();
                ctx.write(Unpooled.copiedBuffer(response));
                ctx.flush();
                mBuf.release();

                double afterTime = System.currentTimeMillis();
                double secDiffTime = (afterTime - beforeTime) / 1000;
//                log.info("[DATA][TIME] secDiffTime : {}", secDiffTime);

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
