package com.ksptooi.association;

/**
 * 端点操作指令集
 */
public class VK {

    public static byte F_PULL = 1;
    public static byte D_LIST = 2;

    public static byte F_PUSH = 3;
    public static byte F_ABS_PUSH = 31;

    public static byte OPT_FAILED = 105;
    public static byte OPT_SUCCESS = 107;


    /**
     * 主机可接收的指令
     */
    public static byte H_RCV_TXT = 20;
    public static byte H_RCV_PUSH = 21;
    public static byte H_RCV_PUSH_ABS = 22;
    public static byte H_LAUNCH = 22;
    public static byte H_PUSH_LAUNCHERS = 31;

    /**
     * 主机可发送的指令
     */
    public static byte H_SND_TXT = 20;  //多模式文本(TXT,JSON)
    public static byte H_SND_PUSH = 21; //相对路径推送
    public static byte H_SND_PUSH_ABS = 22; //绝对路径推送
    public static byte H_RESULT_PUSH = 25; //推送成功
    public static byte H_FINISH_PUSH = 90; //推送成功
    public static byte H_GET_LAUNCHERS = 30; //加载启动器列表

    public static byte H_PUSH_LAUNCH_SETTINGS = 40; //向服务端推送启动器设定

    public static byte H_GET_LAUNCH_SETTINGS = 41; //向服务端索要启动器设定

    public static byte H_REQUEST_TEMP_DIR = 42; //向服务端请求创建临时文件夹

    public static byte H_RESPONSE_TEMP_DIR = 43; //服务端返回临时文件夹名

    public static byte H_REQUEST_LAUNCH = 50; //客户端请求发射
    public static byte H_RESPONSE_LAUNCH = 51; //服务端返回发射结果

    public static byte H_RESPONSE_PUSH_SETTINGS= 59; //服务端返回结果

    public static byte H_REQUEST_KEEP_ALIVE = 68; //检查存活情况
    public static byte H_RESPONSE_KEEP_ALIVE = 69; //检查存活情况

    public static byte H_GET_LAUNCHER_DECLARER = 75; //请求启动器参数列表
    public static byte H_RESPONSE_LAUNCHER_DECLARER = 76; //响应启动器参数列表

}
