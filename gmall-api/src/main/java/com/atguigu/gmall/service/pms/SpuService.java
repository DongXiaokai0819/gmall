package com.atguigu.gmall.service.pms;

import com.atguigu.gmall.bean.PmsProductImage;
import com.atguigu.gmall.bean.PmsProductInfo;
import com.atguigu.gmall.bean.PmsProductSaleAttr;

import java.util.List;

public interface SpuService {
    //获取spuInfoList列表
    List<PmsProductInfo> getSpuInfoListByCata3Id(String catalog3Id);

    //保存SpuInfo（增加一条）
    Integer saveSpuInfo(PmsProductInfo pmsProductInfo);

    //获取SpuSaleAttr Spu销售属性列表
    List<PmsProductSaleAttr> getSpuSaleAttrList(String productId);

    //获取spu 图片列表
    List<PmsProductImage> getSpuImageList(String productId);

    //通过productId查询销售属性集合
    List<PmsProductSaleAttr> getspuSaleAttrListCheckBySku(String productId,String skuId);
}
