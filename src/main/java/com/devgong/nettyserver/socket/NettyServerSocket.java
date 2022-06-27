package com.devgong.nettyserver.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;


@Slf4j
@RequiredArgsConstructor  //final or @NonNull 인 필드 값만 파라미터로 받는 생성자 만듦
@Component
public class NettyServerSocket {

    /*
    * 네티 서버를 실행하는 클래스입니다.
    * ApplicationStartupTask 클래스에서 스프링부트 서비스를 시작할 떄
    * 보았던 클래스의 start() 메소드를 실행하도록 설정.
    */
    @Autowired
    private final ServerBootstrap serverBootstrap;
    @Autowired
    private final InetSocketAddress tcpPort;
    private Channel serverChannel;

    public void start() {
        try {
            // ChannelFuture: I/O operation의 결과나 상태를 제공하는 객체
            // 지정한 host, port로 소켓을 바인딩하고 incoming connections을 받도록 준비함
//            ChannelFuture serverChannelFuture = serverBootstrap.bind(tcpPort).sync();

            // 서버 소켓이 닫힐 때까지 기다림
            serverChannel = serverBootstrap.bind(tcpPort)
                            .sync()
                            .channel()
                            .closeFuture()
                            .sync()
                            .channel();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Bean을 제거하기 전에 해야할 작업이 있을 때 설정
    @PreDestroy
    public void stop() {
        if (serverChannel != null) {
            serverChannel.close();
            serverChannel.parent().closeFuture();
        }
    }
}
