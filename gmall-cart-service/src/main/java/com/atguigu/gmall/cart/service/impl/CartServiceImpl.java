package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.cart.mapper.OmsCartItemMapper;
import com.atguigu.gmall.service.cart.CartService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OmsCartItemMapper cartItemMapper;


    @Override
    public OmsCartItem ifCartExistByUser(String memberId, String skuId) {
        //通过memberId和skuId确定一个购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        OmsCartItem omsCartItem1 = cartItemMapper.selectOne(omsCartItem);
        return omsCartItem1;
    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {
        System.out.println(omsCartItem.getQuantity());
        if (StringUtils.isNotBlank(omsCartItem.getMemberId())){
            cartItemMapper.insertSelective(omsCartItem);
        }

    }

    @Override
    public void updateCart(OmsCartItem omsCartItemFromDb) {
        Example e = new Example((OmsCartItem.class));
        e.createCriteria().andEqualTo("id",omsCartItemFromDb.getId());
        cartItemMapper.updateByExampleSelective(omsCartItemFromDb,e);
    }

    @Override
    public void flushCartCache(String memberId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        List<OmsCartItem> omsCartItemList = cartItemMapper.select(omsCartItem);

        Jedis jedis = redisUtil.getJedis();
        Map<String,String> map = new HashMap<>();
        for (OmsCartItem cartItem : omsCartItemList) {
            cartItem.setTotalPrice(cartItem.getPrice().multiply(cartItem.getQuantity()));
            map.put(cartItem.getProductSkuId(), JSON.toJSONString(cartItem));
        }

        jedis.del("user:"+memberId+":cart");
        jedis.hmset("user:"+memberId+":cart",map);

        jedis.close();

    }

    @Override
    public List<OmsCartItem> cartList(String userId) {
        Jedis jedis = null;
        List<OmsCartItem> omsCartItemList = new ArrayList<>();

        try {
            jedis = redisUtil.getJedis();
            List<String> hvals = jedis.hvals("user:" + userId + ":cart");
            for (String hval : hvals) {
                OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                omsCartItemList.add(omsCartItem);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return omsCartItemList;
    }

    @Override
    public void checkCart(OmsCartItem omsCartItem) {
        Example e = new Example(OmsCartItem.class);
        e.createCriteria().andEqualTo("memberId",omsCartItem.getMemberId()).andEqualTo("productSkuId",omsCartItem.getProductSkuId());
        cartItemMapper.updateByExampleSelective(omsCartItem,e);

        flushCartCache(omsCartItem.getMemberId());
    }
}
