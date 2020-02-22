package com.atguigu.gmall.interceptor;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.annotations.LoginRequired;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.gmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod hm = (HandlerMethod) handler;
        LoginRequired methodAnnotation = hm.getMethodAnnotation(LoginRequired.class);

        StringBuffer url = request.getRequestURL();
        System.out.println(url);

        //是否拦截
        if (methodAnnotation == null) {
            return true;
        }

        String token = "";

        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        if (StringUtils.isNotBlank(oldToken)) {
            token = oldToken;
        }

        String newToken = request.getParameter("token");
        if (StringUtils.isNotBlank(newToken)) {
            token = newToken;
        }

        //是否必须登录
        boolean loginSuccess = methodAnnotation.loginSuccess();

        //调用认证中心进行验证
        String success = "fail";
        if (StringUtils.isNotBlank(token)) {
            String ip = request.getHeader("x-forwarded-for");//从nginx转发的客户端ip
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();//从request中获取ip
                if (StringUtils.isBlank(ip)) {
                    ip = "127.0.0.1";
                }
            }
            String successJSON = HttpclientUtil.doGet("http://localhost:8085/verify?token=" + token + "&currentIp=" + ip);
            Map<String, String> successMap = JSON.parseObject(successJSON, Map.class);
            success = successMap.get("status");
        }


        if (loginSuccess) {
            //必须登录成功才能使用

            if (!success.equals("success")) {
                //重定向passport登录
                StringBuffer requestURL = request.getRequestURL();
                response.sendRedirect("http://localhost:8085/index?ReturnUrl=" + requestURL);
                return false;

            }
            //验证通过，覆盖cookie中的token
            //需要将token携带的用户信息写入
            request.setAttribute("memberId", "1");
            request.setAttribute("nickname", "nickname");
            //验证通过，覆盖cookie的token
            if (StringUtils.isNotBlank(token)) {
                CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
            }

        } else {
            //可以不登录
            //必须验证
            if (success.equals("success")) {
                request.setAttribute("memberId", "1");
                request.setAttribute("nickname", "nickname");
                //验证通过，覆盖cookie的token
                if (StringUtils.isNotBlank(token)) {
                    CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
                }
            }
        }


        System.out.println("进入拦截器方法");
        return true;
    }
}
