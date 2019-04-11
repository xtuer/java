import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
抽取文章的 tag 和 title，按照 tag 分组生成 sitemap

1. 每篇文章的开头都有下面几行 meta 信息
---
title: 数据库常用基础
date: 2017-05-21 11:45:06
tags: DB
---
---
title: 数据库常用基础
date: 2017-05-21 11:45:06
tags: [DB, Java]
---

2. 抽取 tag 和 title，按照 tag 分组，生成 sitemap.md 格式如下:
---
title: All Documents
date: 2015-06-06 06:06:06
tags: Index
---

## Ajax
* [SpringMVC Ajax 拖拽上传文件](/spring-mvc-upload-file-ajax)
* [SpringMVC 处理 Ajax 映射](/spring-mvc-ajax)

## Cas
* [Spring Security 入门](/java-cas-2-security-intro)
* [Tomcat 启用 https](/java-tomcat-https)
...
*/
public class HexoSitemapGenerator {
    // Hexo Blog 的默认目录
    private static final String BLOG_DIR_PATH = "/Users/Biao/Documents/workspace/Blog/source/_posts";

    // Sitemap 的头部
    private static final String SITEMAP_HEADER = "---\n" +
            "title: All Documents\n" +
            "date: 2015-06-06 06:06:06\n" +
            "tags: Index\n" +
            "---\n\n";

    public static void main(String[] args) throws IOException {
        // 1. 获取所有的 md 文件
        // 2. 遍历每一个 md 文件，抽取 title 和 tags
        // 3. 生成 sitemap.md
        //    3.1 按 tag 分组
        //    3.2 生成 sitemap.md 的内容
        //    3.3 保存 sitemap.md 到文件

        final String blogDir = System.getProperty("dir", BLOG_DIR_PATH);

        // [1] 获取所有的 md 文件
        File[] mds = new File(blogDir).listFiles((dir, name) -> name.toLowerCase().endsWith(".md"));

        // [2] 遍历每一个 md 文件，抽取 title 和 tags
        List<Meta> metas = new LinkedList<>();
        for (File md : mds) {
            metas.add(extractMeta(md));
        }

        // [3.1] 按 tag 分组
        Map<String, List<Meta>> map = new TreeMap<>(); // key 为 tag, value 为具有相同 tag 的 meta
        for (Meta meta : metas) {
            for (String tag : meta.tags) {
                map.putIfAbsent(tag, new LinkedList<>());
                map.get(tag).add(meta);
            }
        }

        // [3.2] 生成 sitemap.md 的内容
        StringBuilder out = new StringBuilder(SITEMAP_HEADER);
        map.forEach((tag, tagMetas) -> {
            tagMetas.sort(Comparator.comparing(Meta::getTitle)); // 按照 title 对 metas 进行排序

            out.append(String.format("## %s\n", tag));           // tag 作为二级标题, 把 Blog 分类排列
            for (Meta meta : tagMetas) {
                out.append(String.format("* [%s](%s)\n", meta.title, meta.url));
            }
            out.append("\n");
        });
        System.out.println(out);

        // [3.3] 保存 sitemap.md 到文件
        FileUtils.writeStringToFile(new File(blogDir + "/sitemap.md"), out.toString(), "UTF-8");
    }

    private static Meta extractMeta(File md) throws IOException {
        // 访问前 10 行，提取 title 和 tag
        Meta meta = new Meta(md);
        int i = 0;

        for (String line : FileUtils.readLines(md, "UTF-8")) {
            int start = line.indexOf(':');

            if (line.startsWith("title:")) {
                meta.title = line.substring(start + 1).trim();
            } else if (line.startsWith("tags:")) {
                // a. 找到 tags: 后面的字符串
                // b. 去掉 [ 和 ]
                // c. 使用 , 拆分得到数组
                // d. 去掉前后的空格
                // e. 转为 set
                meta.tags = Stream.of(line.substring(start + 1).replaceAll("[\\[\\]]", "").split(","))
                        .map(String::trim)
                        .collect(Collectors.toSet());
                break;
            }

            if (++i > 10) {
                break;
            }
        }

        // 没有设置 tag，归类到 tag NoTag
        if (meta.tags == null) {
            meta.tags = new HashSet<>();
            meta.tags.add("zz-NoTag");
        }

        return meta;
    }

    // 博客的 md 文件的元数据
    @Data
    static class Meta {
        public String url;       // 博客的 url，linux-crontab.md 的 url 为 /linux-crontab
        public String title;     // 博客的标题
        public Set<String> tags; // 博客的 tag 集合

        Meta(File md) {
            url = "/" + FilenameUtils.getBaseName(md.getName()) + "/"; // 根据文件名生成 url
        }
    }
}
