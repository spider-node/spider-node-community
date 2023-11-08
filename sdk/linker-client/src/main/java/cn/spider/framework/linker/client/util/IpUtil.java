package cn.spider.framework.linker.client.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.linker.client.util
 * @Author: dengdongsheng
 * @CreateTime: 2023-04-02  18:14
 * @Description: TODO
 * @Version: 1.0
 */
public class IpUtil {
    public static String buildLocalHost() throws SocketException {
        Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
        while (nifs.hasMoreElements()) {
            NetworkInterface nif = nifs.nextElement();
            Enumeration<InetAddress> address = nif.getInetAddresses();
            while (address.hasMoreElements()) {
                InetAddress addr = address.nextElement();
                if (addr instanceof Inet4Address) {
                    if(addr.getHostAddress().equals("127.0.0.1")){
                        continue;
                    }
                    return addr.getHostAddress();
                }
            }
        }
        return null;
    }
}
