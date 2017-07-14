package com.xtuer.service;

import com.alibaba.fastjson.JSON;
import com.xtuer.bean.KnowledgePoint;
import com.xtuer.bean.Paper;
import com.xtuer.bean.PaperDirectory;
import com.xtuer.mapper.KnowledgePointMapper;
import com.xtuer.mapper.PaperDirectoryMapper;
import com.xtuer.mapper.PaperMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Service
public class PaperExportService {
    private static Logger logger = LoggerFactory.getLogger(PaperExportService.class);

    public static final String EXPORT_STATUS   = "paper_export_status";
    public static final String EXPORT_RUNNING  = "paper_export_running";  // 正在执行导出
    public static final String EXPORT_FINISHED = "paper_export_finished"; // 导出结束

    @Resource(name="config")
    private Properties config;

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private PaperDirectoryMapper directoryMapper;

    @Autowired
    private KnowledgePointMapper knowledgePointMapper;

    @Resource(name="redisTemplate")
    private StringRedisTemplate redisTemplate;

    /**
     * 导出试卷
     * @param paperDirectoryIds
     * @return 操作的状态: EXPORT_RUNNING 或则 EXPORT_FINISHED
     */
    @Async
    public void exportPapers(List<String> paperDirectoryIds) {
        if (paperDirectoryIds.isEmpty()) {
            logger.info("需要导出的目录为空");
            redisTemplate.opsForValue().set(EXPORT_STATUS, EXPORT_FINISHED, 1, TimeUnit.MINUTES);
            return;
        }

        // 导出状态为 export_running 则不允许执行导出
        if (isExporting()) {
            logger.info("正在导出，请稍后再操作");
            return;
        }

        logger.info("开始导出试卷");
        exporting();

        // 1. 导出所有知识点
        // 2. 导出所有试卷目录
        // 3. 导出目录下试卷用到的知识点关系
        // 4. 导出目录下的试卷
        // 5. 复制试卷文件

        try {
            // 创建导出目录
            String time = DateFormatUtils.format(new Date(), "yyyyMMdd-HHmmss");
            File exportDirectory = new File(config.getProperty("paper.exportDirectory"), time);
            FileUtils.forceMkdir(exportDirectory);
            int passStatus = 1; // 导出已通过的试卷，status 为 1

            // [1] 导出所有知识点
            List<KnowledgePoint> knowledgePoints = knowledgePointMapper.getAllKnowledgePoints();
            saveToFile(knowledgePoints, exportDirectory, "knowledgePoints.json");

            // [2] 导出所有试卷目录
            List<PaperDirectory> paperDirectories = directoryMapper.getAllPaperDirectories();
            saveToFile(paperDirectories, exportDirectory, "paperDirectories.json");

            // [3] 导出目录下试卷用到的知识点关系
            List<KnowledgePoint> paperKnowledgePointRelation = paperMapper.findPaperKnowledgePointsRelationInPaperDirectories(paperDirectoryIds, passStatus);
            saveToFile(paperKnowledgePointRelation, exportDirectory, "paperKnowledgePointRelation.json");

            // [4] 导出目录下试卷用到的知识点关系
            // [5] 复制文件
            List<Paper> papers = paperMapper.findPapersInPaperDirectories(paperDirectoryIds, passStatus);
            savePapers(papers, exportDirectory, "papers.json");
        } catch (IOException e) {
            logger.info("导出文件出错: {}", e.getMessage());
            logger.info(ExceptionUtils.getStackTrace(e));
        }

        // 向 Redis 写入导入完成标记 EXPORT_STATUS: EXPORT_FINISHED
        logger.info("结束导出试卷");
        exportFinished();

        return;
    }

    private void saveToFile(Object obj, File directory, String fileName) throws IOException {
        OutputStream out = new FileOutputStream(new File(directory, fileName));
        JSON.writeJSONString(out, obj);
        IOUtils.closeQuietly(out);
    }

    private void savePapers(List<Paper> papers, File directory, String fileName) throws IOException {
        OutputStream out = new FileOutputStream(new File(directory, fileName), true); // 试卷 json 输出流

        File paperDir = new File(config.getProperty("paper.paperDirectory")); // 试卷文件的目录
        File exportDir = new File(directory, "papers"); // 导出试卷的目录

        for (Paper paper : papers) {
            exporting(); // 因为写文件可能比较长，所以每写一次都写一次导出状态

            // 把试卷的 Json 数据写入文件
            paper.setKnowledgePoints(null);
            String json = JSON.toJSONString(paper, false) + "\n";
            IOUtils.write(json, out);

            // 复制试卷到导出目录
            File src = new File(new File(paperDir, paper.getRealDirectoryName()), paper.getUuidName());
            File dst = new File(exportDir, paper.getRealDirectoryName());

            try {
                FileUtils.copyFileToDirectory(src, dst);
            } catch (IOException e) {
                // 文件不存在时抛出异常
                logger.info(e.getMessage());
            }
        }

        IOUtils.closeQuietly(out);
    }

    // 当前执行导出的状态
    public String exportStatus() {
        String exportStatus = redisTemplate.opsForValue().get(EXPORT_STATUS);
        return EXPORT_RUNNING.equals(exportStatus) ? EXPORT_RUNNING : EXPORT_FINISHED;
    }

    // 是否正在导出
    public boolean isExporting() {
        String exportStatus = redisTemplate.opsForValue().get(EXPORT_STATUS);
        return EXPORT_RUNNING.equals(exportStatus);
    }

    // 向 Redis 写入正在导入标记: EXPORT_STATUS: EXPORT_RUNNING
    private void exporting() {
        redisTemplate.opsForValue().set(EXPORT_STATUS, EXPORT_RUNNING, 1, TimeUnit.MINUTES); // 1 分钟后超时
    }

    // 向 Redis 写入导入完成标记 EXPORT_STATUS: EXPORT_FINISHED
    private void exportFinished() {
        redisTemplate.opsForValue().set(EXPORT_STATUS, EXPORT_FINISHED, 1, TimeUnit.MINUTES);
    }
}
