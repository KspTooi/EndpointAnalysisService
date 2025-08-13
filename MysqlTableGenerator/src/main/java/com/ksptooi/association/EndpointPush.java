package com.ksptooi.association;

import java.io.File;
import java.io.IOException;

public class EndpointPush {

    public static void main(String[] args) throws IOException {
        //var client = new ForkClient("127.0.0.1",59000);
        var client = new ForkClient("192.168.10.42",59000);
        client.connect();
        //client.pushFile("/生成器模板",new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity_vue\\ns_confidential\\methods.ts"));
        //client.pushFile("/生成器模板",new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity_vue\\ns_confidential\\model.vm"));
        //client.pushFile("/生成器模板",new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity_vue\\ns_confidential\\operable.vue"));
        //client.pushFile("/生成器模板",new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity_vue\\ns_confidential\\view.vm"));

        client.pushFile("/",new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\artifact.art"));
    }
}
