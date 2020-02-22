package com.atguigu.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.annotations.LoginRequired;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.pms.AttrService;
import com.atguigu.gmall.service.search.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SearchController {

    @Reference
    private SearchService searchService;

    @Reference
    private AttrService attrService;

    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap) {// 三级分类id、关键字、

        // 调用搜索服务，返回搜索结果
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list(pmsSearchParam);
        modelMap.put("skuLsInfoList", pmsSearchSkuInfos);

        // 抽取检索结果锁包含的平台属性集合
        Set<String> valueIdSet = new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                String valueId = pmsSkuAttrValue.getValueId();
                valueIdSet.add(valueId);
            }
        }
        // 根据valueId将属性列表查询出来
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.getAttrValueByAttrId(valueIdSet);
        modelMap.put("attrList", pmsBaseAttrInfos);

        // 对平台属性集合进一步处理，去掉当前条件中valueId所在的属性组
        String[] delValueIds = pmsSearchParam.getValueId();
        if (delValueIds != null) {
            // 面包屑
            // pmsSearchParam
            // delValueIds
            List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
            for (String delValueId : delValueIds) {
                Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                // 生成面包屑的参数
                pmsSearchCrumb.setValueId(delValueId);
                pmsSearchCrumb.setUrlParam(getUrlParam(pmsSearchParam, delValueId));
                while (iterator.hasNext()) {
                    PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                    List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                    for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                        String valueId = pmsBaseAttrValue.getId();
                        if (delValueId.equals(valueId)) {
                            // 查找面包屑的属性值名称
                            pmsSearchCrumb.setValueName(pmsBaseAttrValue.getValueName());
                            //删除该属性值所在的属性组
                            iterator.remove();
                        }
                    }
                }
                pmsSearchCrumbs.add(pmsSearchCrumb);
            }
            modelMap.put("attrValueSelectedList", pmsSearchCrumbs);
        }
        String urlParam = getUrlParam(pmsSearchParam);
        modelMap.put("urlParam", urlParam);
        String keyword = pmsSearchParam.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            modelMap.put("keyword", keyword);
        }
        return "list";
    }

//        private String getUrlParam(PmsSearchParam pmsSearchParam) {
//        String keyword = pmsSearchParam.getKeyword();
//        String catalog3Id = pmsSearchParam.getCatalog3Id();
//        String[] skuAttrValueArray = pmsSearchParam.getValueId();
//
//        String urlParam = "";
//
//        if (StringUtils.isNotBlank(keyword)) {
//            if (StringUtils.isNotBlank(urlParam)){
//                urlParam = urlParam + "&";
//            }
//            urlParam = urlParam + "keyword=" + keyword;
//        }
//        if (StringUtils.isNotBlank(catalog3Id)) {
//            if (StringUtils.isNotBlank(urlParam)){
//                urlParam = urlParam + "&";
//            }
//            urlParam = urlParam + "catalog3Id=" + catalog3Id;
//        }
//        if (skuAttrValueArray != null) {
//            for (String pmsSkuAttrValue : skuAttrValueArray) {
//                urlParam = urlParam + "&valueId=" + pmsSkuAttrValue;
//            }
//        }
//        return urlParam;
//    }

    private String getUrlParam(PmsSearchParam pmsSearchParam, String... delValueId) {
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] skuAttrValueArray = pmsSearchParam.getValueId();

        String urlParam = "";

        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isNotBlank(urlParam)) {
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "keyword=" + keyword;
        }
        if (StringUtils.isNotBlank(catalog3Id)) {
            if (StringUtils.isNotBlank(urlParam)) {
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "catalog3Id=" + catalog3Id;
        }
        if (skuAttrValueArray != null) {
            for (String pmsSkuAttrValue : skuAttrValueArray) {
                if (delValueId.length == 1) {
                    if (!pmsSkuAttrValue.equals(delValueId[0])) {
                        urlParam = urlParam + "&valueId=" + pmsSkuAttrValue;
                    }
                } else {
                    urlParam = urlParam + "&valueId=" + pmsSkuAttrValue;
                }
            }
        }
        return urlParam;
    }


    @RequestMapping("index")
    @LoginRequired(loginSuccess = false)
    public String index() {
        return "index";
    }
}
