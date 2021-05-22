package com.atguigu.yygh.cmn.service.impl;


import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/15 20:07
 */

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    //根据数据 id 查询子数据列表
    @Override
    //keyGenerator key自定义名字生成函数
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public List<Dict> findChildData(long id) {

        List<Dict> dictList = baseMapper.selectList(new QueryWrapper<Dict>()
                .eq("parent_id", id)
        );
        //设置 list 集合中的hasChridren字段
        //这一段有待优化，又查询了一次数据库
        for (Dict dict : dictList) {
            boolean isChild = this.hasChildren(dict.getId());
            dict.setHasChildren(isChild);
        }
        return dictList;
    }

    //导出数据字典接口
    @Override
    public void exportDictData(HttpServletResponse response) {
        //设置下载信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = "dict";
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");
        //查询数据库
        List<Dict> dictList = baseMapper.selectList(null);
        //Dict -- DictEeVo 使用流的方式
        List<DictEeVo> dictEeVoList = dictList.stream().map(temp -> {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(temp, dictEeVo);
            return dictEeVo;
        }).collect(Collectors.toList());

        //调用方法进行写操作
        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict")
                    .doWrite(dictEeVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //导入数据字典
    @Override
    //清空缓存中的内容
    @CacheEvict(value = "dict", allEntries=true)
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //根据 dictcode 和 value 查询
    @Override
    public String getDictName(String dictCode, String value) {
        //dickcode 为空，直接根据value 查
        if (StringUtils.isEmpty(dictCode)) {
            return baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("value", value)).getName();
        } else {
            //这里写个 xml 不香吗
            //dictcode 不为空，说明传的是一个大类下的 value
            //这个方法代表可能 value 相等 parent_id 不等，直接和上面的方法矛盾了
            Long parent_id = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("dict_code", dictCode)).getId();
            return baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", parent_id)
                    .eq("value", value)).getName();
        }
    }

    //判断 id 下面是否有子节点
    private boolean hasChildren(Long id) {
        Integer count = baseMapper.selectCount(new QueryWrapper<Dict>()
                .eq("parent_id", id)
        );
        return count > 0;
    }


}
