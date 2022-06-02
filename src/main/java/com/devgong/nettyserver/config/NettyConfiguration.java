package com.devgong.nettyserver.config;

import com.devgong.nettyserver.socket.NettyChannelInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
@RequiredArgsConstructor
public class NettyConfiguration {

/*
* 네티 설정을 위한 클래스입니다.
* @Value 어노테이션으로 스프링의 설정 파일(application.yml 혹은 application.properties)을 읽어 옵니다.
*/

    @Value("${server.host}") // ex) 127.0.0.1
    private String host;
    @Value("${server.port}") // ex) 9999
    private int port;
    @Value("${server.netty.boss-count}") // ex) 1
    private int bossCount;
    @Value("${server.netty.worker-count}") // ex) 10
    private int workerCount;
    @Value("${server.netty.keep-alive}") // ex) true
    private boolean keepAlive;
    @Value("${server.netty.backlog}") // ex) 100
    private int backlog;

    @Bean
    public ServerBootstrap serverBootstrap(NettyChannelInitializer nettyChannelInitializer) {
        // ServerBootstrap: 서버 설정을 도와주는 class
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup())
                // NioServerSocketChannel: incoming connections를 수락하기 위해 새로운 Channel을 객체화할 때 사용
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                // ChannelInitializer: 새로운 Channel을 구성할 때 사용되는 특별한 handler. 주로 ChannelPipeline으로 구성
                .childHandler(nettyChannelInitializer);

        //ServerBootstarp에 다양한 Option 추가 가능
        //SO_BACKLOG: 동시에 수용 가능한 최대 incoming connections 개수  --> leakMaster의 커넥팅 개수는?--> 4096개로 잡혀있음.
        //SO_KEEPALIVE : 기본적으로 SO_KEEPALIVE 옵션은 서버측 소켓에 설정되어 상대방 시스템의 고장이나 정전, 네트워크 연결이 끊기는 등 통신이 불가능한 상황을 탐지해줌.
        // 일정시간동안 해당 소켓을통해 어떤 자료도 송수신되지 않을 시, 커널에서 상대방의 상태를 확인하는 패킷을 전송. 상대방이 정상이면 ACK 전송
        // TCP_NODELAY 등 옵션 제공
        b.option(ChannelOption.SO_BACKLOG, backlog); // 동시에 512개의 클라이언트 요총을 받아들이겠다는 의미.
        return b;
    }

    // boss: incoming connection을 수락하고, 수락한 connection을 worker에게 등록(register)
    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(bossCount);
    }

    // worker: boss가 수락한 연결의 트래픽 관리
    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(workerCount);
    }

    // IP 소켓 주소(IP 주소, Port 번호)를 구현
    // 도메인 이름으로 객체 생성 가능
    @Bean
    public InetSocketAddress inetSocketAddress() {
        return new InetSocketAddress(host, port);
    }
}
