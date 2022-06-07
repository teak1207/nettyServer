package com.devgong.nettyserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(NettyServerApplication.class, args);
        // 메인 메서드가 선언된 클래스로, 스프링부트가 시작되는 지점
    }

}
