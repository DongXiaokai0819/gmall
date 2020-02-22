package com.atguigu.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsProductImage;
import com.atguigu.gmall.bean.PmsProductInfo;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.bean.PmsProductSaleAttrValue;
import com.atguigu.gmall.order.mapper.SpuImageMapper;
import com.atguigu.gmall.order.mapper.SpuInfoMapper;
import com.atguigu.gmall.order.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.order.mapper.SpuSaleAttrValueMapper;
import com.atguigu.gmall.service.pms.SpuService;
import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuInfoMapper infoMapper;

    @Autowired
    private SpuSaleAttrMapper saleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper saleAttrValueMapper;

    @Autowired
    private SpuImageMapper imageMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<PmsProductInfo> getSpuInfoListByCata3Id(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        return infoMapper.select(pmsProductInfo);
    }

    @Override
    public Integer saveSpuInfo(PmsProductInfo pmsProductInfo) {
        // 保存商品信息
        infoMapper.insertSelective(pmsProductInfo);

        // 生成商品主键
        String productId = pmsProductInfo.getId();

        // 保存商品图片信息
        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
        for (PmsProductImage pmsProductImage : spuImageList) {
            pmsProductImage.setProductId(productId);
            imageMapper.insertSelective(pmsProductImage);
        }

        // 保存销售属性信息
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
            pmsProductSaleAttr.setProductId(productId);
            saleAttrMapper.insertSelective(pmsProductSaleAttr);

            // 保存销售属性值
            List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                pmsProductSaleAttrValue.setProductId(productId);
                saleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
            }
        }
        return 1;
    }

    @Override
    public List<PmsProductSaleAttr> getSpuSaleAttrList(String productId) {
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(productId);
        List<PmsProductSaleAttr> spuSaleAttrList = saleAttrMapper.select(pmsProductSaleAttr);
        for (PmsProductSaleAttr spuSaleAttr : spuSaleAttrList) {
            PmsProductSaleAttrValue spuSaleAttrValue = new PmsProductSaleAttrValue();
            spuSaleAttrValue.setProductId(productId);
            spuSaleAttrValue.setSaleAttrId(spuSaleAttr.getSaleAttrId());

            List<PmsProductSaleAttrValue> spuSaleAttrValueList = saleAttrValueMapper.select(spuSaleAttrValue);
            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValueList);
        }
        return spuSaleAttrList;
    }

    @Override
    public List<PmsProductImage> getSpuImageList(String productId) {
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(productId);
        return imageMapper.select(pmsProductImage);
    }

    @Override
    public List<PmsProductSaleAttr> getspuSaleAttrListCheckBySku(String productId, String skuId) {
//        PmsProductSaleAttr saleAttr = new PmsProductSaleAttr();
//        saleAttr.setProductId(productId);
//        List<PmsProductSaleAttr> spuSaleAttrList = saleAttrMapper.select(saleAttr);
//
//        for (PmsProductSaleAttr productSaleAttr : spuSaleAttrList) {
//            String saleAttrId = productSaleAttr.getSaleAttrId();
//
//            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
//            pmsProductSaleAttrValue.setSaleAttrId(saleAttrId);
//            pmsProductSaleAttrValue.setProductId(productId);
//            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = saleAttrValueMapper.select(pmsProductSaleAttrValue);
//            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
//    }
        return saleAttrMapper.selectSpuSaleAttrListCheckBySku(productId,skuId);
    }
}
