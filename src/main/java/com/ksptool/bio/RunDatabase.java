package com.ksptool.bio;


import com.ksptooi.commons.H2Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;

public class RunDatabase {

    public static void main(String[] args) throws SQLException {

        var port = 59000;

        if (!isPortInUse(port)) {
            H2Server h2Server = new H2Server(59000);
            h2Server.start();
        }

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
