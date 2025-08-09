package com.ksptooi;

import com.ksptooi.commons.H2Server;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;

@SpringBootApplication
@EnableScheduling
public class EASRunnerWithH2DB {

    private static H2Server h2Server = null;


    public static void main(String[] args) throws SQLException {

        var port = 59000;

        if (!isPortInUse(port)) {
            h2Server = new H2Server(59000);
            h2Server.start();
        }

        EASRunner.main(args);
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
