package com.xtuer.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class PaperImporter {
    public static void main(String[] args) throws Exception {
        String subject = "高中语文";
        File paperDir  = new File("/Users/Biao/Documents/套卷/高中语文（2804套）/GZYW033C");
        File destDir   = new File("/Users/Biao/Documents/套卷/papers");
        int paperCount = 15439; // 数据库里已有试卷的数量

        importPaperToDb(subject, paperDir, destDir, paperCount);
    }

    public static void importPaperToDb(String subject, File paperDir, File destDir, int paperCount) throws Exception {
        List<File> papers = Arrays.asList(paperDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".doc"))); // doc 文件
        Collections.sort(papers, (a, b) -> a.getName().compareTo(b.getName())); // 对文件名进行排序

        Connection conn = getConnection();
        PreparedStatement pstmt = null;
        conn.setAutoCommit(false);

        for (File paper : papers) {
            // name, uuid_name, original_name, real_directory_name, subject
            String name = paper.getName();
            String originalName = name;
            String uuidName = CommonUtils.uuid() + "." + FilenameUtils.getExtension(name);
            String realDirectoryName = "" + paperCount/500; // 每个目录放 500 个试卷

            try {
                pstmt = conn.prepareStatement("INSERT INTO paper(paper_id, name, uuid_name, original_name, real_directory_name, subject) VALUES(?, ?, ?, ?, ?, ?)");
                pstmt.setString(1, CommonUtils.uuid());
                pstmt.setString(2, name);
                pstmt.setString(3, uuidName);
                pstmt.setString(4, originalName);
                pstmt.setString(5, realDirectoryName);
                pstmt.setString(6, subject);
                pstmt.execute();
                conn.commit();

                // move file
                File finalDir = new File(destDir, realDirectoryName);
                FileUtils.moveFile(paper, new File(finalDir, uuidName));

                ++paperCount;
            } catch (Exception ex) {
                ex.printStackTrace();
                conn.rollback();
            } finally {
                close(null, pstmt, null);
            }
        }

        close(conn, null, null);

        System.out.println(paperCount);
    }

    /*******************************************************************************
     *                                   Common                                    *
     ******************************************************************************/
    private static String url = "jdbc:mysql://localhost:3306/paper_suite?useUnicode=true&characterEncoding=UTF-8";
    private static String username = "root";
    private static String password = "root";

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("加载 MySQL 驱动出错");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception ex) {}
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception ex) {}
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ex) {}
        }
    }
}
