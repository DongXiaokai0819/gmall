package com.atguigu.gmall.service.pms;

import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import com.atguigu.gmall.bean.PmsBaseAttrValue;
import com.atguigu.gmall.bean.PmsBaseSaleAttr;

import java.util.List;
import java.util.Set;

public interface AttrService {
    //获取属性列表
    List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id);

    //增加属性
    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    //获取属性值列表
    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    //获取基本商品属性列表
    List<PmsBaseSaleAttr> getSaleAttrList();

    //通过attrId列表，查询base_attr_info 和base_attr_value
    List<PmsBaseAttrInfo> getAttrValueByAttrId(Set<String> attrId);
}
