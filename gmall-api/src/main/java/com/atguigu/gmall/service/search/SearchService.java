package com.atguigu.gmall.service.search;

import com.atguigu.gmall.bean.PmsSearchParam;
import com.atguigu.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {
    //获取商品信息列表
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
