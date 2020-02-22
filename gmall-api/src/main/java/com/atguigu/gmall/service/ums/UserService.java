package com.atguigu.gmall.service.ums;

import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {

    //获取所有UserMember列表
    List<UmsMember> getUmsMemberList();

    //根据id获取UserMember
    UmsMember getUmsberById(String id);

    //根据id删除UserMember
    Integer deleteUmsMemberById(String id);

    //增加UserMember
    Integer addUmsMember(UmsMember umsMember);

    //修改UserMember
    Integer alterUmsMember(UmsMember umsMember);

    //根据memberId获取收获地址列表
    List<UmsMemberReceiveAddress> getUmsAddressByMemberId(String memberId);

    //根据id获取UmsAddress
    UmsMemberReceiveAddress getUmsAddressById(String id);

    //根据id删除UmsAddress
    Integer deleteUmsAddressById(String id);

    //增加UmsAddress
    Integer addUmsAddress(UmsMemberReceiveAddress umsAddress);

    //修改UmsAddress
    Integer alterUmsAddress(UmsMemberReceiveAddress umsAddress);

    UmsMember login(UmsMember umsMember);

    void addUserToken(String token, String memberId);

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);

    UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId);
}
