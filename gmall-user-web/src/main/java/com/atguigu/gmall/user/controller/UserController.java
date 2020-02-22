package com.atguigu.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.ums.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/ums")
public class UserController {

    @Reference
    UserService userService;

    @RequestMapping("getUmsAddressByMemberId")
    @ResponseBody
    public List<UmsMemberReceiveAddress> getUmsAddressByMemberId(String memberId) {
        return userService.getUmsAddressByMemberId(memberId);
    }

    @RequestMapping("getUmsberById")
    @ResponseBody
    public UmsMember getUmsberById(String id) {
        return userService.getUmsberById(id);
    }

    @RequestMapping("getUmsMemberList")
    @ResponseBody
    public List<UmsMember> getAllUser() {
        return userService.getUmsMemberList();
    }

    @RequestMapping("deleteUmsMemberById")
    @ResponseBody
    public Integer deleteUmsMemberById(String id) {
        return userService.deleteUmsMemberById(id);
    }

    @RequestMapping("addUmsMember")
    @ResponseBody
    public Integer addUmsMember(UmsMember umsMember) {
        return userService.addUmsMember(umsMember);
    }

    @RequestMapping("alterUmsMember")
    @ResponseBody
    public Integer alterUmsMember(UmsMember umsMember) {
        return userService.alterUmsMember(umsMember);
    }

    @RequestMapping("getUmsAddressById")
    @ResponseBody
    public UmsMemberReceiveAddress getUmsAddressById(String id) {
        return userService.getUmsAddressById(id);
    }

    @RequestMapping("deleteUmsAddressById")
    @ResponseBody
    public Integer deleteUmsAddressById(String id) {
        return userService.deleteUmsAddressById(id);
    }

    @RequestMapping("addUmsAddress")
    @ResponseBody
    public Integer addUmsAddress(UmsMemberReceiveAddress umsAddress) {
        return userService.addUmsAddress(umsAddress);
    }

    @RequestMapping("alterUmsAddress")
    @ResponseBody
    public Integer alterUmsAddress(UmsMemberReceiveAddress umsAddress) {
        return userService.alterUmsAddress(umsAddress);
    }

}
