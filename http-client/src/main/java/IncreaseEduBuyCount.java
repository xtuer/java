import org.apache.http.client.fluent.Request;

import java.util.Random;

public class IncreaseEduBuyCount {
    public static void increase(String url) throws Exception {
        System.out.println(url + System.currentTimeMillis());
        Request.Get(url + System.currentTimeMillis()).connectTimeout(2000).socketTimeout(2000).execute();
        Thread.sleep(1000);
    }

    public static void main(String[] args) throws Exception {
        String[] urls = {
                "空谈",
                "http://i.edu-edu.com.cn/sale/public/comments/counter/add/1/2691/1/jsonp/__sale_product_counter_loaded?_=",
                "http://i.edu-edu.com.cn/sale/public/comments/counter/add/1/2691/2/jsonp/__sale_product_counter_loaded?_=",

                "IBM 数据大学",
                "http://i.edu-edu.com.cn/sale/public/comments/counter/add/1/2690/1/jsonp/__sale_product_counter_loaded?_=",
                "http://i.edu-edu.com.cn/sale/public/comments/counter/add/1/2690/2/jsonp/__sale_product_counter_loaded?_=",

                "IBM R 语言",
                "http://i.edu-edu.com.cn/sale/public/comments/counter/add/1/2684/1/jsonp/__sale_product_counter_loaded?_=",
                "http://i.edu-edu.com.cn/sale/public/comments/counter/add/1/2684/2/jsonp/__sale_product_counter_loaded?_="
        };


        Random random = new Random();

        for (int i = 6; i < urls.length; i += 3) {
            System.out.println(i);
            for (int j = 0; j < 200; ++j) {
                System.out.println("J: " + j);

                if (random.nextInt(100) > 60) {
                    increase(urls[i+1]);
                }

                if (random.nextInt(100) > 40) {
                    increase(urls[i+2]);
                }
            }
        }
    }
}
