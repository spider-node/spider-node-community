package cn.spider.framework.common.utils;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.common.utils
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-07  19:57
 * @Description: TODO
 * @Version: 1.0
 */
public class BrokerInfoUtil {
    public static String queryBrokerName(Vertx vertx){
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        return localMap.get("broker-name");
    }

    public static String queryBrokerIp(Vertx vertx){
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");

        return localMap.get("broker-ip");
    }

    public static int queryTranscriptNum(Vertx vertx){
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        return Integer.parseInt(localMap.get("broker-transcript-num"));
    }

    public static String queryRpcType(Vertx vertx){
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        return localMap.get("rpc-type");
    }

    public static String queryUiPort(Vertx vertx){
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        return localMap.get("ui-port");
    }


    public static String queryFunctionPort(Vertx vertx){
        SharedData sharedData = vertx.sharedData();
        LocalMap<String, String> localMap = sharedData.getLocalMap("config");
        return localMap.get("function-port");
    }




}
