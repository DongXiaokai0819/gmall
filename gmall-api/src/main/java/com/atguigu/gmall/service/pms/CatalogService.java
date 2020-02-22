package com.atguigu.gmall.service.pms;

import com.atguigu.gmall.bean.PmsBaseCatalog1;
import com.atguigu.gmall.bean.PmsBaseCatalog2;
import com.atguigu.gmall.bean.PmsBaseCatalog3;

import java.util.List;

public interface CatalogService {
    //一级分类列表
    List<PmsBaseCatalog1> getCatalog1List();

    //二级分类列表
    List<PmsBaseCatalog2> getCatalog2List(String catalog1Id);

    //三级分类列表
    List<PmsBaseCatalog3> getCatalog3List(String catalog2Id);

}
