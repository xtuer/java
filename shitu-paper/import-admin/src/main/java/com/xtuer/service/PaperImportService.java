package com.xtuer.service;

import com.xtuer.bean.Paper;
import com.xtuer.mapper.ImporterMapper;
import com.xtuer.util.CommonUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 试卷导入服务
 */
public class PaperImportService {
    @Autowired
    private ImporterMapper mapper;

    /**
     * 导入试卷信息到数据库，并移动试卷到指定目录。
     *
     * @param papers
     * @param destDirectory
     * @param subject
     */
    @Transactional
    public void importPaper(List<File> papers, File destDirectory, String subject) {
        for (File paperFile : papers) {
            // name, uuid_name, original_name, real_directory_name, subject
            String name = paperFile.getName();
            String originalName = name;
            String uuid = CommonUtils.uuid();
            String uuidName = uuid + "." + FilenameUtils.getExtension(name);
            String realDirectoryName = CommonUtils.directoryNameByUuid(uuid);

            Paper paper = new Paper();
            paper.setPaperId(uuid)
                    .setName(name)
                    .setOriginalName(name)
                    .setUuidName(uuidName)
                    .setRealDirectoryName(realDirectoryName)
                    .setSubject(subject);

            mapper.insertPaper(paper); // 插入试卷到数据库

            try {
                // 复制试卷到目录
                File finalDir = new File(destDirectory, realDirectoryName);
                FileUtils.copyFile(paperFile, new File(finalDir, uuidName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取目录下的试卷
     *
     * @param paperDirectory 试卷的目录
     * @return 试卷的 list
     */
    public static List<File> listPapers(String paperDirectory) {
        List<File> papers = Arrays.asList(new File(paperDirectory).listFiles((dir, name) -> name.toLowerCase().endsWith(".doc")));
        Collections.sort(papers, (a, b) -> a.getName().compareTo(b.getName())); // 对文件名进行排序

        return papers;
    }

    /**
     * 更新试卷的属性
     *
     * @param path 属性文件的路径
     * @param subject 试卷所属学科
     * @throws Exception
     */
    @Transactional
    public void updatePapersMeta(String path, String subject) throws Exception {
        Reader reader = new InputStreamReader(new FileInputStream(path));
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);

        for (CSVRecord record : records) {
            String paperName = record.get("FixPaperName").trim();
            String paperYear = record.get("PaperYear").trim();
            String paperRegion = record.get("PaperRegion").trim();
            String paperFrom = record.get("PaperFrom").trim();
            String paperType = record.get("PaperType").trim();
            String description = record.get("memo").trim();
            String originalPaperId = record.get("FixPaperID").trim();

            Paper paper = new Paper();
            paper.setOriginalName(paperName + ".doc")
                    .setPublishYear(paperYear)
                    .setRegion(paperRegion)
                    .setPaperFrom(paperFrom)
                    .setPaperType(paperType)
                    .setSubject(subject)
                    .setDescription(description);

            mapper.updatePaperMeta(paper);
        }
    }
}
