package com.xtuer.controller;

import com.alibaba.fastjson.JSONObject;
import com.mzlion.easyokhttp.HttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

@Controller
public class WeiXinQY {
    // 保存以下变量是为了用微信测试时方便，不需要手动去输入
    public static String accessToken = null;
    public static String code = null;
    public static String userTicket = null;
    public static String userId = null;

    // 获取 access token
    @GetMapping("/login/token")
    @ResponseBody
    public String token() {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ww78b1ca2d41e767e3&corpsecret=OPvGF7jESdwam00dMVlHXaxSMFt0_q_jI-Pl37GBXsQ";
        String responseData = HttpClient.get(url).execute().asString();
        JSONObject obj = JSONObject.parseObject(responseData);
        accessToken = obj.getString("access_token");

        return accessToken;
    }

    // 授权登陆页
    @GetMapping("/login/weixinqy")
    public String loginQY() {
        String callback = URLEncoder.encode("http://buch.ngrok.cc/login/weixinqy/callback");
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=ww78b1ca2d41e767e3&redirect_uri=" + callback + "&response_type=code&scope=snsapi_privateinfo&agentid=1000002#wechat_redirect";

        return "redirect:" + url;
    }

    // 登陆成功后得到 code
    @GetMapping("/login/weixinqy/callback")
    @ResponseBody
    public Object loginQYCallback(HttpServletRequest request) {
        System.out.println("code: " + request.getParameter("code"));
        code = request.getParameter("code");
        return request.getParameterMap();
    }

    // 获取成员基础信息
    @GetMapping("/api/user-info")
    @ResponseBody
    public String userInfo() {
        String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=%s&code=%s", accessToken, code);
        String responseData = HttpClient.get(url).execute().asString();
        JSONObject obj = JSONObject.parseObject(responseData);
        userTicket = obj.getString("user_ticket");
        userId = obj.getString("UserId");

        System.out.println(responseData);
        return responseData;
    }

    // 获取成员详情
    @GetMapping("/api/user-details")
    @ResponseBody
    public String userDetails() {
        String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s", accessToken, userId);
        String responseData = HttpClient.get(url).execute().asString();

        System.out.println(responseData);
        return responseData;
    }
}
