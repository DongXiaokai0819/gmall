package com.atguigu.gmall.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.ums.UserService;
import com.atguigu.gmall.user.mapper.UmsMapper;
import com.atguigu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UmsMapper umsMapper;

    @Autowired
    UmsMemberReceiveAddressMapper umsAddressMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {

        // 封装的参数对象
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsAddressMapper.select(umsMemberReceiveAddress);


//       Example example = new Example(UmsMemberReceiveAddress.class);
//       example.createCriteria().andEqualTo("memberId",memberId);
//       List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(example);

        return umsMemberReceiveAddresses;
    }

    @Override
    public UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(receiveAddressId);
        UmsMemberReceiveAddress umsMemberReceiveAddress1 = umsAddressMapper.selectOne(umsMemberReceiveAddress);

        return umsMemberReceiveAddress1;
    }

    @Override
    public List<UmsMember> getUmsMemberList() {
        return umsMapper.selectAll();
    }

    @Override
    public UmsMember getUmsberById(String id) {
        return umsMapper.selectByPrimaryKey(id);
    }

    @Override
    public Integer deleteUmsMemberById(String id) {
        return umsMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Integer addUmsMember(UmsMember umsMember) {
        return umsMapper.insert(umsMember);
    }

    @Override
    public Integer alterUmsMember(UmsMember umsMember) {
        return umsMapper.updateByPrimaryKey(umsMember);
    }

    @Override
    public List<UmsMemberReceiveAddress> getUmsAddressByMemberId(String memberId) {

        // 封装的参数对象
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);

        return umsAddressMapper.select(umsMemberReceiveAddress);
//       Example example = new Example(UmsMemberReceiveAddress.class);
//       example.createCriteria().andEqualTo("memberId",memberId);
//       List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(example);
    }

    @Override
    public UmsMemberReceiveAddress getUmsAddressById(String id) {
        return umsAddressMapper.selectByPrimaryKey(id);
    }

    @Override
    public Integer deleteUmsAddressById(String id) {
        return umsAddressMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Integer addUmsAddress(UmsMemberReceiveAddress umsAddress) {
        return umsAddressMapper.insert(umsAddress);
    }

    @Override
    public Integer alterUmsAddress(UmsMemberReceiveAddress umsAddress) {
        return umsAddressMapper.updateByPrimaryKey(umsAddress);
    }

    @Override
    public UmsMember login(UmsMember umsMember) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            if (jedis != null) {
                String umsMemberStr = jedis.get("user:" + umsMember.getPassword() + umsMember.getUsername()+ ":info");

                if (StringUtils.isNotBlank(umsMemberStr)) {
                    //密码正确
                    UmsMember umsMemberFromCache = JSON.parseObject(umsMemberStr, UmsMember.class);
                    return umsMemberFromCache;
                }
            }
            UmsMember umsMemberFromDb = loginFromDb(umsMember);
            if (umsMemberFromDb != null) {
                jedis.setex("user:" + umsMember.getPassword() + umsMember.getUsername() + ":info", 60 * 60 * 24, JSON.toJSONString(umsMemberFromDb));
            }
            return umsMemberFromDb;
        } finally {
            jedis.close();
        }

    }

    @Override
    public void addUserToken(String token, String memberId) {
        Jedis jedis = redisUtil.getJedis();
        jedis.setex("user:" + memberId + ":token", 60 * 60 * 2, token);
        jedis.close();
    }

    private UmsMember loginFromDb(UmsMember umsMember) {
        List<UmsMember> select = umsMapper.select(umsMember);
        if (select != null) {
            return select.get(0);
        }
        return null;

    }
}
