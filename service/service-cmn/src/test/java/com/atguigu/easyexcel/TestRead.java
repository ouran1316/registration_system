package com.atguigu.easyexcel;

import com.alibaba.excel.EasyExcel;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/18 20:59
 */
public class TestRead {
    public static void main(String[] args) {
        //读取文件路径
        String fileName = "E:\\01.xlsx";
        //调用方法实现读取操作
        EasyExcel.read(fileName, UserData.class, new ExcelListener()).sheet().doRead();
    }
}
