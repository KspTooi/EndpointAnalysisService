package com.ksptooi;

import com.ksptooi.commons.H2Server;
import com.ksptooi.commons.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.net.ServerSocket;

@SpringBootApplication
@EnableScheduling
public class RASRunner implements ApplicationRunner {

    @Autowired
    private H2Server h2Server;

    public static void main(String[] args) {
        SpringApplication.run(RASRunner.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //h2Server.start();
    }

    public static boolean isPortInUse(int port) {
        try {
            new ServerSocket(port).close();
            return false;
        } catch (IOException e) {
            return true;
        }
    }

}
