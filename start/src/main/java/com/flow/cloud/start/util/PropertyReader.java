package com.flow.cloud.start.util;

import java.io.*;
import java.util.*;

/**
 * @program: spider
 * @description: 读取spider-conf
 * @author: dds
 * @create: 2022-09-04 14:02
 */

public class PropertyReader {
    private final static String BASEPATH = PropertyReader.class.getResource("/").getFile();

    //根据Key读取Value
    public static String GetValueByKey(String filename, String key) {
        Properties pps = new Properties();
        try {

            InputStream in = new BufferedInputStream(new FileInputStream(BASEPATH+filename));
            pps.load(in);
            String value = pps.getProperty(key)+"";
            if(("null").equals(value)) {
                return null;
            }
            in.close();
            return value;
        }catch (IOException e) {
//            e.printStackTrace();
            return null;
        }
    }
    //读取Properties的全部信息
    public static Map<String, String> GetAllProperties(String filename) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            Properties pps = new Properties();
            InputStream in = new BufferedInputStream(new FileInputStream(BASEPATH+filename));
            pps.load(in);
            Enumeration en = pps.propertyNames(); //得到配置文件的名字
            while(en.hasMoreElements()) {
                String strKey = (String) en.nextElement();
                String strValue = pps.getProperty(strKey);
                map.put(strKey, strValue);
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return  map;
    }

    //写入Properties信息
    public static void WriteProperties (String filename, String pKey, String pValue) throws IOException {
        Properties pps = new Properties();
        InputStream in = new FileInputStream(BASEPATH+filename);
        //从输入流中读取属性列表（键和元素对）
        pps.load(in);
        //调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
        //强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
        OutputStream out = new FileOutputStream(BASEPATH+filename);
        pps.setProperty(pKey, pValue);
        //以适合使用 load 方法加载到 Properties 表中的格式，
        //将此 Properties 表中的属性列表（键和元素对）写入输出流
        pps.store(out, "Update " + pKey + " name");
        out.flush();
        in.close();
        out.close();
    }

    //批量写入Properties信息
    public static void WritePropertiesBatch (String filename, List<String> list, Map<String,String> map) throws IOException {
        Properties pps = new Properties();
        InputStream in = new FileInputStream(BASEPATH+filename);
        InputStreamReader inputStreamReader = new InputStreamReader(in,"UTF-8");
        //从输入流中读取属性列表（键和元素对）
        pps.load(inputStreamReader);
        OutputStream out = new FileOutputStream(BASEPATH+filename);
        for(String str : list) {
            pps.setProperty(str, map.get(str));
        }
        pps.store(out, "Update");
        out.flush();
        in.close();
        out.close();
    }
}
