import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sd4324530.fastweixin.api.entity.Menu;
import com.github.sd4324530.fastweixin.api.entity.MenuButton;
import com.github.sd4324530.fastweixin.api.enums.MenuType;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class TestMenuJson {
    @Test
    public void menuToJson() throws JsonProcessingException {
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

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        System.out.println(mapper.writeValueAsString(menu));
    }

    /**
     * 使用对象创建菜单太麻烦, 把表示菜单的字符串反序列化为菜单对象就方便许多。
     * 菜单的字符串格式:
        {
            "button": [{
                "type": "CLICK",
                "name": "主菜单一",
                "key": "main1"
            }, {
                "type": "VIEW",
                "name": "主菜单二",
                "url": "http://www.baidu.com"
            }, {
                "type": "CLICK",
                "name": "真题",
                "subButton": [{
                    "type": "CLICK",
                    "name": "2016 语文",
                    "key": "sub1"
                }, {
                    "type": "CLICK",
                    "name": "2016 数学",
                    "key": "sub2"
                }]
            }]
        }
     */
    @Test
    public void jsonToMenu() throws IOException {
        String json = "{\n" +
                "    \"button\": [{\n" +
                "        \"type\": \"CLICK\",\n" +
                "        \"name\": \"今日歌曲\",\n" +
                "        \"key\": \"V1001_TODAY_MUSIC\"\n" +
                "    }, {\n" +
                "        \"name\": \"菜单\",\n" +
                "        \"subButton\": [{\n" +
                "            \"type\": \"VIEW\",\n" +
                "            \"name\": \"搜索\",\n" +
                "            \"url\": \"http://www.soso.com/\"\n" +
                "        }, {\n" +
                "            \"type\": \"VIEW\",\n" +
                "            \"name\": \"视频\",\n" +
                "            \"url\": \"http://v.qq.com/\"\n" +
                "        }, {\n" +
                "            \"type\": \"CLICK\",\n" +
                "            \"name\": \"赞一下我们\",\n" +
                "            \"key\": \"V1001_GOOD\"\n" +
                "        }]\n" +
                "    }]\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        Menu menu = mapper.readValue(json, Menu.class);
        System.out.println(mapper.writeValueAsString(menu));
    }
}
