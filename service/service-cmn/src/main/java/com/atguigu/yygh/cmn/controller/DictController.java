package com.atguigu.yygh.cmn.controller;

import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.Get;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/18 14:35
 */
@Api(value = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
//@CrossOrigin
public class DictController {

    @Autowired
    DictService dictService;

    //导入数据字典
    @PostMapping("/importData")
    public Result importDict(MultipartFile file) {
        dictService.importDictData(file);
        return Result.ok();
    }

    //导出数据字典接口
    @GetMapping("/exportData")
    public void exportDict(HttpServletResponse response) {
        dictService.exportDictData(response);
    }

    //根据dictCode获取下级节点
    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping("findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode) {
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }

    //根据数据 id 查询子数据列表
    @ApiOperation("根据数据 id 查询子数据列表")
    @GetMapping("/findChildData/{id}")
    public Result findChildData(@PathVariable("id") long id) {
        List<Dict> list = dictService.findChildData(id);
        return Result.ok(list);
    }

    //根据 dictcode 和 value 查询
    @GetMapping("/getName/{dictCode}/{value}")
    public String getName(@PathVariable("dictCode") String dictCode,
                          @PathVariable("value") String value) {
        return dictService.getDictName(dictCode, value);
    }

    //根据 value 查询
    @GetMapping("/getName/{value}")
    public String getName(@PathVariable("value") String value) {
        return dictService.getDictName("", value);
    }
}
