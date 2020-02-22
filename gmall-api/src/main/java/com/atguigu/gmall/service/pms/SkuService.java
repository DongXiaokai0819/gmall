package com.atguigu.gmall.service.pms;

import com.atguigu.gmall.bean.PmsProductInfo;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;

import java.math.BigDecimal;
import java.util.List;

public interface SkuService {
    //获取skuInfoList列表
//    List<PmsSkuInfo> getSkuInfoListByProductId(String productId);

    //保存skuInfo
    void saveSkuInfo(PmsSkuInfo skuInfo);

    //根据id获取PmsSkuInfo
    PmsSkuInfo getSkuById(String skuId);

    //根据productId（SpuId）获取SkuInfo列表
    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId);

    //根据productId（SpuId）获取SkuInfo列表
    List<PmsSkuInfo> getAllSkuInfo(String catalog3Id);

    boolean checkPrice(String productSkuId, BigDecimal price);
}
