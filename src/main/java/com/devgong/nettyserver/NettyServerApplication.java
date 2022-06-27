package com.devgong.nettyserver;

import com.devgong.nettyserver.socket.NettyServerSocket;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyServerApplication {

    public static void main(String[] args) {

        // 메인 메서드가 선언된 클래스로, 스프링부트가 시작되는 지점
        SpringApplication.run(NettyServerApplication.class, args);

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        /*이벤트 루프(eventLoop)
          이벤트 처리 방법으로 네티의 이벤트 루프는 큐를 공유하지 않고,
          채널은 하나의 이벤트 루프에만 등록되기 때문에 처리 순서가 보장.
          서버 생성 시, boss와 worker 이벤트 그룹을 생성하는데 인자값으로
          스레드 갯수를 지정 할 수 있음. 만약 childGroup을 주지 않으면
          parent그룹이 worker그룹도 병행
        */


    }

}
