import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

public class BlogSitemapGenerator {
    // Blog 的目录
    private static final String BLOG_DIR_PATH = "/Users/Biao/Documents/workspace/Blog/source/_posts";

    // key 是 Blog 的 tag
    private static Map<String, List<BlogMeta>> metas = new TreeMap<String, List<BlogMeta>>();

    public static void main(String[] args) throws Exception {
        // [1]. 取得所有的 Blog
        File[] blogs = getBlogs(BLOG_DIR_PATH);

        // [2]. 生成 tag 和其对应的 BlogMeta 的 map
        for (File blog : blogs) {
            BlogMeta meta = extractBlogMeta(blog);

            if (meta == null) {
                continue;
            }

            if (meta.tags.size() > 0) {
                // 有多个 tag
                for (String tag : meta.tags) {
                    List<BlogMeta> subMetas = getBlogMetasByTag(tag);
                    subMetas.add(meta);
                }
            } else {
                // 没有 tag, 放到 Default 下
                List<BlogMeta> subMetas = getBlogMetasByTag("Default");
                subMetas.add(meta);
            }
        }

        // [3]. 生成 Site Map 的 Blog
        StringBuilder out = new StringBuilder("---\n" +
                "title: All Documents\n" +
                "date: 2015-06-06 06:06:06\n" +
                "tags: Index\n" +
                "---\n\n");

        for (String tag : metas.keySet()) {
            List<BlogMeta> subMetas = metas.get(tag);
            out.append(String.format("\n## %s\n", tag)); // tag 作为二级标题, 把 Blog 分类排列

            for (BlogMeta meta : subMetas) {
                out.append(String.format("* [%s](%s)\n", meta.title, meta.url));
            }
        }

        System.out.println(out);

        // [4]. 输出到文件
        FileUtils.writeStringToFile(new File(BLOG_DIR_PATH + "/sitemap.md"), out.toString(), "UTF-8");
    }

    /**
     * 列出所有 Blog 的文件, 后缀为 .md
     *
     * @param blogDirPath Blog 所在文件夹的目录
     * @return
     */
    private static File[] getBlogs(String blogDirPath) {
        File[] mds = new File(blogDirPath).listFiles((dir, name) -> {
            return name.toLowerCase().endsWith("md");
        });

        return mds;
    }

    /**
     * 提取 Blog 的元数据
     *
     * @param blog
     * @return
     * @throws Exception
     */
    private static BlogMeta extractBlogMeta(File blog) throws Exception {
        Scanner scanner = new Scanner(blog);
        BlogMeta meta = new BlogMeta();
        String[] tags = null;
        int count = 0;

        meta.url = "/" + FilenameUtils.getBaseName(blog.getName());

        // 获取 title 和 tags (为了简单起见, 没有校验给定的文件是否 blog)
        while (scanner.hasNextLine() && count++ < 10) { // 只取前 10 行
            String line = scanner.nextLine();
            int start = line.indexOf(':');

            if (line.startsWith("title:")) {
                meta.title = line.substring(start + 1).trim();
            } else if (line.startsWith("tags:")) {
                tags = line.substring(start + 1).replaceAll("\\[|\\]", "").split(",");
                break;
            }
        }

        for (String tag : tags) {
            tag = StringUtils.trim(tag);

            if (!StringUtils.isBlank(tag)) {
                meta.tags.add(tag);
            }
        }

        return meta;
    }

    /**
     * 返回 metas 中 tag 对应的 list, 如果不存在则创建
     * @param tag
     * @return
     */
    private static List<BlogMeta> getBlogMetasByTag(String tag) {
        List<BlogMeta> subMetas = metas.get(tag);

        if (subMetas == null) {
            subMetas = new LinkedList<BlogMeta>();
            metas.put(tag, subMetas);
        }

        return subMetas;
    }
}

/**
 * Blog 的元数据
 */
class BlogMeta {
    public String url;
    public String title;
    public List<String> tags = new LinkedList<String>();
}
