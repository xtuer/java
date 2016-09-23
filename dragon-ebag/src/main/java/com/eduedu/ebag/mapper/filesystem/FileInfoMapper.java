package com.eduedu.ebag.mapper.filesystem;

import com.eduedu.ebag.bean.filesystem.FileInfo;
import org.apache.ibatis.annotations.Param;

public interface FileInfoMapper {
    FileInfo selectFileInfoByFileNodeId(int id);
    FileInfo selectFileInfoByMd5(@Param("md5") String md5);

    void insertFileInfo(FileInfo fileInfo);
    boolean isMd5AlreadyUsed(@Param("md5") String md5);
}
