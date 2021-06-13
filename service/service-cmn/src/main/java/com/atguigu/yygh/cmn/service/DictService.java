package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author ouran
 * @Version 1.0
 * @Date 2021/5/15 20:06
 */
public interface DictService extends IService<Dict> {

    //根据数据 id 查询子数据列表
    List<Dict> findChildData(long id);

    //导入数据字典
    void exportDictData(HttpServletResponse response);

    //导出数据字典
    void importDictData(MultipartFile file);

    //根据 dictcode 和 value 查询
    String getDictName(String dictCode, String value);

    //根据dictCode获取下级节点
    List<Dict> findByDictCode(String dictCode);
}
