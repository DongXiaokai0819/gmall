package com.atguigu.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsSkuAttrValue;
import com.atguigu.gmall.bean.PmsSkuImage;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.order.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.order.mapper.SkuImageMapper;
import com.atguigu.gmall.order.mapper.SkuInfoMapper;
import com.atguigu.gmall.order.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.service.pms.SkuService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    RedisUtil redisUtil;


    @Override
    public void saveSkuInfo(PmsSkuInfo skuInfo) {

        // 插入skuInfo
        int i = skuInfoMapper.insertSelective(skuInfo);
        String skuId = skuInfo.getId();

        // 插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insertSelective(skuAttrValue);
        }

        // 插入销售属性关联
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
        }

        // 插入图片信息
        List<PmsSkuImage> skuImageList = skuInfo.getSkuImageList();
        for (PmsSkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insertSelective(skuImage);
        }
    }

    //从数据库中查询信息
    public PmsSkuInfo getSkuByIdFromDb(String skuId){
        // sku商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = skuInfoMapper.selectOne(pmsSkuInfo);

        // sku的图片集合
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = skuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);
        return skuInfo;
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        Jedis jedis = redisUtil.getJedis();
        String skuJson = jedis.get("sku:" + skuId + ":info");
        if (StringUtils.isNoneBlank(skuJson)){
            pmsSkuInfo = JSON.parseObject(skuJson,PmsSkuInfo.class);
        }else {
            //防止缓存击穿、缓存穿透

            //设置分布式锁
            String OK = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 10);
            if (StringUtils.isNotBlank(OK)&&OK.equals("OK")){
                //设置成功
                //获取到分布式锁后，即为有权进行下一步操作
                pmsSkuInfo = this.getSkuByIdFromDb(skuId);
                if (pmsSkuInfo!=null){
                    jedis.set("sku:"+skuId+":info",JSON.toJSONString(pmsSkuInfo));
                }else {//若对对象为空，防止缓存穿透，设置一个空值，并赋予过期时间
                    jedis.setex("sku:"+skuId+":info",3*60,JSON.toJSONString(""));
                }
                jedis.del("sku:"+skuId+":info");
            }else {
                //设置失败的话，重新尝试获取分布式锁，自旋操作
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuById(skuId);
            }
        }
        jedis.close();
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
//        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
//        pmsSkuInfo.setProductId(productId);
//        List<PmsSkuInfo> select = skuInfoMapper.select(pmsSkuInfo);
        return skuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
    }

    @Override
    public List<PmsSkuInfo> getAllSkuInfo(String catalog3Id) {
        List<PmsSkuInfo> skuInfoList = skuInfoMapper.selectAll();
        for (PmsSkuInfo skuInfo : skuInfoList) {
            PmsSkuAttrValue skuAttrValue = new PmsSkuAttrValue();
            skuAttrValue.setSkuId(skuInfo.getId());
            List<PmsSkuAttrValue> skuAttrValueList = skuAttrValueMapper.select(skuAttrValue);
            skuInfo.setSkuAttrValueList(skuAttrValueList);
        }
        return skuInfoList;
    }

    @Override
    public boolean checkPrice(String productSkuId, BigDecimal productPrice) {

        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(productSkuId);
        PmsSkuInfo pmsSkuInfo1 = skuInfoMapper.selectOne(pmsSkuInfo);
        BigDecimal price1 = pmsSkuInfo1.getPrice();
        return price1.compareTo(productPrice) == 0;
    }
}
