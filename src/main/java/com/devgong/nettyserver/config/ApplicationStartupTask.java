package com.devgong.nettyserver.config;

import com.devgong.nettyserver.socket.NettyServerSocket;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationStartupTask implements ApplicationListener<ApplicationReadyEvent> {
/*
ApplicationReadyEvent : 스프링 부트 서비스를 시작 시, 초기화하는 코드를 Bean으로 만들 때 사용합니다
여기서는 네티 서버 소켓을 실행하여 incoming connection을 받을 준비를 합니다.
*/


    private final NettyServerSocket nettyServerSocket;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        nettyServerSocket.start();
    }
}
