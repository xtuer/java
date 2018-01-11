package com.xtuer.service;

import com.xtuer.bean.Paper;
import com.xtuer.mapper.PaperMapper;
import com.xtuer.util.DocToHtmlUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.File;
import java.util.Properties;

@Service
public class PaperService {
    private static Logger logger = LoggerFactory.getLogger(PaperService.class);

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private ServletContext servletContext;

    @Resource(name = "config")
    private Properties config;

    /**
     * 获取试卷的预览文件的 URL。
     *
     * @param paperId 试卷的 id
     * @return 预览文件的 URL
     */
    public String getPaperPreviewUrl(String paperId) throws Exception {
        // 1. 查询试卷信息
        // 2. 检查是否已经生成预览的 html，如果没有生成则生成

        Paper paper = paperMapper.findPaperByPaperId(paperId);
        File paperDoc = getPaperFile(paper); // 试卷文件
        File paperPreviewHtml = getPaperPreviewIndex(paper); // 预览文件

        // 如果试卷的预览文件不存在，则创建
        if (!paperPreviewHtml.exists()) {
            if (!paperDoc.exists()) {
                logger.warn("试卷文件不存在: " + paperDoc.getAbsolutePath());
                return null;
            }

            logger.debug("开始创建试卷预览文件: " + paperDoc.getAbsolutePath());
            DocToHtmlUtils.convertDocToHtml(paperDoc, paperPreviewHtml.getParentFile());
            logger.debug("完成创建试卷预览文件: " + paperPreviewHtml.getAbsolutePath());
        }

        return getPaperPreviewUrl(paper);
    }

    /**
     * 取得试卷预览文件的 URL
     *
     * @param paper
     * @return 返回试卷预览文件的 URL
     */
    public String getPaperPreviewUrl(Paper paper) {
        // String realDirectory = paper.getRealDirectoryName(); // 试卷的上一级目录
        String paperName = paper.getUuidName(); // 试卷文件名字，包含后缀
        String paperBaseName = FilenameUtils.getBaseName(paperName); // 试卷文件基础名，不包含后缀

        return "/preview/" + paperBaseName + "/index.html";
    }

    /**
     * 取得试卷文件
     *
     * @param paper
     * @return 返回试卷文件
     */
    public File getPaperFile(Paper paper) {
        // String realDir = paper.getRealDirectoryName(); // 试卷的上一级目录
        String paperName = paper.getUuidName(); // 试卷文件名字，包含后缀
        String paperDir = config.getProperty("paper.paperDirectory"); // 试卷的所在目录

        return new File(paperDir, paperName); // 试卷文件
    }

    /**
     * 取得试卷预览文件
     *
     * @param paper
     * @return 返回试卷预览文件
     */
    public File getPaperPreviewIndex(Paper paper) {
        return new File(getPaperPreviewDirectory(paper), "index.html"); // 预览文件
    }

    public File getPaperPreviewIndex(String paperBaseName) {
        return new File(getPaperPreviewDirectory(paperBaseName), "index.html");
    }

    /**
     * 取得试卷预览目录
     *
     * @param paper
     * @return 返回试卷预览目录
     */
    public File getPaperPreviewDirectory(Paper paper) {
        String realDir = paper.getRealDirectoryName(); // 试卷的上一级目录
        String paperName = paper.getUuidName(); // 试卷文件名字，包含后缀
        String paperBaseName = FilenameUtils.getBaseName(paperName); // 试卷文件基础名，不包含后缀

        return getPaperPreviewDirectory(paperBaseName);
    }

    /**
     * 取得试卷预览目录
     *
     * @param paperBaseName 试卷名字，不包含后缀
     * @return
     */
    public File getPaperPreviewDirectory(String paperBaseName) {
        String paperPreviewDir = config.getProperty("paper.previewDirectory") + "/" + paperBaseName; // 预览文件所在目录
        return new File(paperPreviewDir);
    }

    /**
     * 取得试卷预览的图片
     *
     * @param paperBaseName
     * @param imageName
     * @return
     */
    public File getPaperPreviewImage(String paperBaseName, String imageName) {
        File previewDirectory = getPaperPreviewDirectory(paperBaseName);
        File imageDirectory = new File(previewDirectory, "img");
        return new File(imageDirectory, imageName);
    }
}
