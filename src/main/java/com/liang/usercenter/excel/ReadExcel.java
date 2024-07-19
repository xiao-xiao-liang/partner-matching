package com.liang.usercenter.excel;

import com.alibaba.excel.EasyExcel;

import java.util.List;
import java.util.Map;

public class ReadExcel {
    /**
     * 最简单的读
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link UserInfo}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link DataListener}
     * <p>
     * 3. 直接读即可
     */
    public static void main(String[] args) {
        String filename = "E:\\星球项目\\伙伴匹配系统\\后端\\partner-matching\\src\\main\\resources\\testExcel.xlsx";
//        readByListener(filename);
        synchronousRead(filename);
    }

    // 写法1：JDK8+ ,不用额外写一个DemoDataListener

    /**
     * 监听器读取
     * @param fileName 文件名
     */
    public static void readByListener(String fileName) {
        EasyExcel.read(fileName, UserInfo.class, new DataListener()).sheet().doRead();
    }

    /**
     * 同步的返回，不推荐使用，如果数据量大会把数据放到内存里面
     */
    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        /*List<UserInfo> list = EasyExcel.read(fileName).head(UserInfo.class).sheet().doReadSync();
        for (UserInfo data : list) {
            System.out.println(data);
        }*/
        // 这里 也可以不指定class，返回一个list，然后读取第一个sheet 同步读取会自动finish
//        List<Map<Integer, String>> listMap = EasyExcel.read(fileName).sheet().doReadSync();
        /*for (Map<Integer, String> data : listMap) {
            // 返回每条数据的键值对 表示所在的列 和所在列的值
            LOGGER.info("读取到数据:{}", JSON.toJSONString(data));
        }*/
    }
}
