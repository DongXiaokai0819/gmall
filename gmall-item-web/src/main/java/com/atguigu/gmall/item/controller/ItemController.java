package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.service.pms.SkuService;
import com.atguigu.gmall.service.pms.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//刷新th页面，ctrl+shitft+f9
@Controller
public class ItemController {

    @Reference
    private SkuService skuService;

    @Reference
    private SpuService spuService;

    @RequestMapping("/{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap map){
        PmsSkuInfo skuInfo = skuService.getSkuById(skuId);
        map.addAttribute("skuInfo",skuInfo);

        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.getspuSaleAttrListCheckBySku(skuInfo.getProductId(),skuId);
        map.addAttribute("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);

        List<PmsSkuInfo> skuInfoList = skuService.getSkuSaleAttrValueListBySpu(skuInfo.getProductId());
        Map<String, String> skuSaleAttrHash = new HashMap<>();
        for (PmsSkuInfo info : skuInfoList) {
            String key = "";
            String value = info.getId();
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = info.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                key += skuSaleAttrValue.getSaleAttrValueId() + "|";
            }
            skuSaleAttrHash.put(key, value);
        }
        // 将sku的销售属性hash表放到页面
        String skuSaleAttrHashJsonStr = JSON.toJSONString(skuSaleAttrHash);
        map.put("skuSaleAttrHashJsonStr",skuSaleAttrHashJsonStr);
        return "item";
    }
}
