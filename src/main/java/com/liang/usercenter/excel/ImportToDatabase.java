package com.liang.usercenter.excel;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ImportToDatabase {
    public static void main(String[] args) {
        String filename = "E:\\星球项目\\伙伴匹配系统\\后端\\partner-matching\\src\\main\\resources\\testExcel.xlsx";
        List<UserInfo> list = EasyExcel.read(filename).head(UserInfo.class).sheet().doReadSync();
        System.out.println("总数：" + list.size());
        for (UserInfo data : list) {
            System.out.println(data);
        }
        Map<String, List<UserInfo>> listMap = list.stream()
                .filter(userInfo -> StringUtils.isNotEmpty(userInfo.getUsername()))
                .collect(Collectors.groupingBy(UserInfo::getUsername));
        for (Map.Entry<String, List<UserInfo>> entry : listMap.entrySet()) {
            System.out.println("username = " +  entry.getKey());
        }
    }
}
