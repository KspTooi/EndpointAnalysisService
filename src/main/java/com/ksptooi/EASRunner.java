package com.ksptooi;

import com.ksptooi.biz.core.service.RelayServerService;
import com.ksptooi.commons.H2Server;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.net.ServerSocket;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class EASRunner{

    @Autowired
    private RelayServerService relayServerService;

    public static void main(String[] args) {
        SpringApplication.run(EASRunner.class, args);
    }


    @PostConstruct
    public void init() {
        relayServerService.initRelayServer();
    }

}
