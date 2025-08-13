package com.ksptooi.association;

public class EndpointLauncher {


    public static void main(String[] args) {
        var endpoint = new ForkEndpoint(59000);
        endpoint.start();
    }

}
