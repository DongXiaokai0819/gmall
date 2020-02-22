package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.service.ums.UserService;
import com.atguigu.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    private UserService userService;

    @RequestMapping("/login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request) {

        String token = "";

        //调用用户服务，验证用户名和密码
        UmsMember umsMemberLogin = userService.login(umsMember);

        if (umsMemberLogin != null) {
            //登录成功
            //利用JWT制作token
            String memberId = umsMember.getId();
            String nickname = umsMember.getNickname();
            Map<String, Object> map = new HashMap<>();
            map.put("memberId", memberId);
            map.put("nickname", nickname);

            String ip = request.getHeader("x-forwarded-for");//从nginx转发的客户端ip
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();//从request中获取ip
                if (StringUtils.isBlank(ip)) {
                    ip = "127.0.0.1";
                }
            }

            //按照设计的算法对参数进行加密后生成token
            token = JwtUtil.encode("2019gmall0105", map, ip);

            //将token存入redis一份
            userService.addUserToken(token, memberId);
        } else {
            //登录失败
            token = "fail";
        }
        return token;
    }


    @RequestMapping("/verify")
    @ResponseBody
    public String verify(String token, String currentIp) {

        Map<String, String> map = new HashMap<>();

        //通过Jwt校验token的真假
        //按照设计的算法对参数进行加密后生成token
        Map<String, Object> decode = JwtUtil.decode(token, "2019gmall0105", currentIp);

        if (decode != null) {
            map.put("status", "success");
            map.put("memberId", (String) decode.get("memberId"));
            map.put("nickname", (String) decode.get("nickname"));
        } else {
            map.put("status", "fail");
        }
        return JSON.toJSONString(map);
    }

    @RequestMapping("/index")
    public String index(String ReturnUrl, ModelMap map) {
        map.put("ReturnUrl", ReturnUrl);
        return "index";
    }
}
