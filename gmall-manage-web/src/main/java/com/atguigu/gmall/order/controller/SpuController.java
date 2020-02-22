package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsProductImage;
import com.atguigu.gmall.bean.PmsProductInfo;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.order.utils.ImageUtil;
import com.atguigu.gmall.service.pms.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@CrossOrigin
public class SpuController {

    @Reference
    private SpuService spuService;

    @RequestMapping("/fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile){
        return ImageUtil.uplocalImage(multipartFile);
    }

    @RequestMapping("/spuList")
    @ResponseBody
    public List<PmsProductInfo> getSpuInfoListByCata3Id(@RequestParam("catalog3Id") String catalog3Id){
        return spuService.getSpuInfoListByCata3Id(catalog3Id);
    }

    @RequestMapping("/saveSpuInfo")
    @ResponseBody
    public Integer saveSpuInfo(@RequestBody PmsProductInfo spuInfo){
        return spuService.saveSpuInfo(spuInfo);
    }

    @RequestMapping("/spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> getSpuSaleAttrList(@RequestParam("spuId") String productId){
        return spuService.getSpuSaleAttrList(productId);
    }

    @RequestMapping("/spuImageList")
    @ResponseBody
    public List<PmsProductImage> getSpuImageList(@RequestParam("spuId") String productId){
        return spuService.getSpuImageList(productId);
    }
}
