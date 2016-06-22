package com.xtuer.controller;

import com.github.sd4324530.fastweixin.api.MediaAPI;
import com.github.sd4324530.fastweixin.api.MenuAPI;
import com.github.sd4324530.fastweixin.api.MessageAPI;
import com.github.sd4324530.fastweixin.api.config.ApiConfig;
import com.github.sd4324530.fastweixin.api.entity.Menu;
import com.github.sd4324530.fastweixin.api.entity.MenuButton;
import com.github.sd4324530.fastweixin.api.enums.MediaType;
import com.github.sd4324530.fastweixin.api.enums.MenuType;
import com.github.sd4324530.fastweixin.api.enums.ResultType;
import com.github.sd4324530.fastweixin.api.response.GetSendMessageResponse;
import com.github.sd4324530.fastweixin.api.response.UploadMediaResponse;
import com.github.sd4324530.fastweixin.message.*;
import com.github.sd4324530.fastweixin.message.req.MenuEvent;
import com.github.sd4324530.fastweixin.message.req.TextReqMsg;
import com.github.sd4324530.fastweixin.servlet.WeixinControllerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;

@RestController
@RequestMapping("/weixin")
public class WeixinController extends WeixinControllerSupport {
    private static final Logger LOG = LoggerFactory.getLogger(WeixinController.class);
    private static final String TOKEN      = "xtuer";
    private static final String APP_ID     = "wx3bfe0aef33141da0";
    private static final String APP_SECRET = "71c73c0508b91678cf52761424bbaf9a";

    // 重写父类方法，处理对应的微信消息
    @Override
    protected BaseMsg handleTextMsg(TextReqMsg msg) {
        String content = msg.getContent(); // content 是用户输入的信息

        switch (content.toUpperCase()) {
            case "URL":
                // 消息中有链接
                return new TextMsg("你好: <a href=\"http://www.baidu.com\">百度</a>");
            case "ATOM":
                // 图文消息
                String picUrl = "http://image.17car.com.cn/image/20120810/20120810092133_13289.jpg"; // 消息中显示的图片
                String url = "http://news.17car.com.cn/saishi/20120810/336283.html"; // 点击消息后跳转的网页的地址
                String description = "700 马力道路赛车 DDMWorks 打造最强 Atom";
                return new NewsMsg(Arrays.asList(new Article("Atom", description, picUrl, url), new Article("Atom", description, picUrl, url)));
            default:
                return new TextMsg("不识别的命令, 您输入的内容是: " + content);
        }
    }

    @Override
    protected BaseMsg handleMenuClickEvent(MenuEvent event) {
        String key = event.getEventKey();
        switch (key.toUpperCase()) {
            case "MAIN1":
                return new TextMsg("点击按钮");
            case "SUB1":
                return new TextMsg("2016 语文");
            case "SUB2":
                return new TextMsg("2016 数学");
            default:
                return new TextMsg("不识别的菜单命令");
        }
    }

    // 设置 TOKEN，用于绑定微信服务器
    @Override
    protected String getToken() {
        return TOKEN;
    }

    // 获取 access token: http://localhost:8080/weixin/access-token
    @GetMapping("/access-token")
    @ResponseBody
    public String getAccessToken() {
        ApiConfig config = new ApiConfig(APP_ID, APP_SECRET);
        return config.getAccessToken();
    }

    // 创建菜单, 访问  http://localhost:8080/weixincreate-menu 就会把菜单信息发送给微信服务器
    @GetMapping("/create-menu")
    @ResponseBody
    public String createMenu() {
        // 准备一级主菜单
        MenuButton main1 = new MenuButton();
        main1.setType(MenuType.CLICK); // 可点击的菜单
        main1.setKey("main1");
        main1.setName("主菜单一");

        MenuButton main2 = new MenuButton();
        main2.setType(MenuType.VIEW); // 链接的菜单，点击后跳转到对应的 URL
        main2.setName("主菜单二");
        main2.setUrl("http://www.baidu.com");

        MenuButton main3 = new MenuButton();
        main3.setType(MenuType.CLICK); // 带有子菜单
        main3.setName("真题");

        // 带有子菜单
        MenuButton sub1 = new MenuButton();
        sub1.setType(MenuType.CLICK);
        sub1.setName("2016 语文");
        sub1.setKey("sub1");

        MenuButton sub2 = new MenuButton();
        sub2.setType(MenuType.CLICK);
        sub2.setName("2016 数学");
        sub2.setKey("sub2");
        main3.setSubButton(Arrays.asList(sub1, sub2));

        Menu menu = new Menu();
        menu.setButton(Arrays.asList(main1, main2, main3));

        //创建菜单
        ApiConfig config = new ApiConfig(APP_ID, APP_SECRET);
        MenuAPI menuAPI = new MenuAPI(config);
        ResultType resultType = menuAPI.createMenu(menu);
        return resultType.toString();
    }
}
