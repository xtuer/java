import bean.PaperDirectory;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import util.Utils;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PaperDirectoryGenerator {
    private static List<PaperDirectory> directories = new LinkedList<>();

    public static void main(String[] args) {
        String baseDirPath = "/Users/Biao/Documents/乐教乐学试题/套卷";
        treeWalk(new File(baseDirPath), "", null);

        // 输出试卷逻辑目录
        System.out.println(JSON.toJSONString(directories));

        // 访问所有的试卷文件
        for (PaperDirectory directory : directories) {
            String dirPath = baseDirPath + directory.getRelativePath();
            Collection<File> docs = FileUtils.listFiles(new File(dirPath), new String[] {"doc"}, false);
            for (File doc : docs) {
                // System.out.println(doc.getAbsolutePath());
            }
        }
    }

    public static void treeWalk(File dir, String relativePath, PaperDirectory parentCategory) {
        PaperDirectory catalog = new PaperDirectory();
        catalog.setPaperDirectoryId(Utils.uuid());

        if (!relativePath.isEmpty()) {
            catalog.setName(dir.getName());
            catalog.setRelativePath(relativePath);
            catalog.setParentPaperDirectoryId(parentCategory.getPaperDirectoryId());
            directories.add(catalog);
        }

        File[] children = dir.listFiles(f -> f.isDirectory());

        for (File child : children) {
            treeWalk(child, relativePath + "/" + child.getName(), catalog);
        }
    }
}
