package com.atguigu.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import com.atguigu.gmall.bean.PmsBaseAttrValue;
import com.atguigu.gmall.bean.PmsBaseSaleAttr;
import com.atguigu.gmall.order.mapper.AttrInfoMapper;
import com.atguigu.gmall.order.mapper.AttrValueMapper;
import com.atguigu.gmall.order.mapper.SaleAttrMapper;
import com.atguigu.gmall.service.pms.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    private AttrInfoMapper infoMapper;

    @Autowired
    private AttrValueMapper valueMapper;

    @Autowired
    private SaleAttrMapper saleAttrMapper;

    @Override
    public List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> attrInfoList = infoMapper.select(pmsBaseAttrInfo);
        for (PmsBaseAttrInfo attrInfo : attrInfoList) {
            String infoId = attrInfo.getId();
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(infoId);
            List<PmsBaseAttrValue> attrValueList = valueMapper.select(pmsBaseAttrValue);
            attrInfo.setAttrValueList(attrValueList);
        }
        return attrInfoList;
    }

    @Override
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        String id = pmsBaseAttrInfo.getId();

        if (StringUtils.isBlank(id)){//如果id不为空，则为新添加的属性
            infoMapper.insertSelective(pmsBaseAttrInfo);//执行插入操作后，pmsBaseAttrInfo会被赋予id
            List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue value : attrValueList){//最好不要循环操作数据库，简单的数据量小的可以
                value.setAttrId(pmsBaseAttrInfo.getId());
                valueMapper.insertSelective(value);
            }
        }else {//如果id为空，则为修改属性
            //info属性表的修改
            Example example = new Example(PmsBaseAttrInfo.class);
            example.createCriteria().andEqualTo("id",id);
            infoMapper.updateByExampleSelective(pmsBaseAttrInfo, example);

            //value表属性值的修改：方法：先删除与attrId相关的行，再增加。。。
            // 感觉有点不太好，先跟着做吧，第二遍的时候再想想

            //删除
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(id);
            valueMapper.delete(pmsBaseAttrValue);

            //将新值插入
            List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
            for (PmsBaseAttrValue value : attrValueList) {
                value.setAttrId(id);
                valueMapper.insertSelective(value);
            }
        }
        return "Success";
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        return valueMapper.select(pmsBaseAttrValue);
    }

    @Override
    public List<PmsBaseSaleAttr> getSaleAttrList() {
        return saleAttrMapper.selectAll();
    }

    @Override
    public List<PmsBaseAttrInfo> getAttrValueByAttrId(Set<String> attrId) {
        String valueIdStr = attrId.stream().collect(Collectors.joining(","));
        return infoMapper.getAttrValueByAttrId(valueIdStr);
    }
}
