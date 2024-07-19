package com.liang.usercenter.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserInfo {
    @ExcelProperty("成员编号")
    private String plantCode;

    @ExcelProperty("成员昵称")
    private String username;
}
