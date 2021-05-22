package com.atguigu.easyexcel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/18 20:47
 */
public class TestWrite {
    public static void main(String[] args) {
        //构建数据 list 集合
        List<UserData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserData data = new UserData();
            data.setUid(i);
            data.setUsername("lucy" + i);
            list.add(data);
        }

        //设置 excel 文件路径和文件名称
        String fileName = "E:\\01.xlsx";

        //调用方法实现写操作
        EasyExcel.write(fileName, UserData.class).sheet("用户信息")
                .doWrite(list);
    }
}
