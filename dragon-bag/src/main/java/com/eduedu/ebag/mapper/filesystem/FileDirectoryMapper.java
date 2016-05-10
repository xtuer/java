package com.eduedu.ebag.mapper.filesystem;

import com.eduedu.ebag.bean.filesystem.FileDirectory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileDirectoryMapper {
    List<FileDirectory> listFilesByDirectoryId(@Param("directoryId") int directoryId,
                                               @Param("userId") int userId);

    List<FileDirectory> listSubdirectoriesByDirectoryId(@Param("directoryId") int directoryId,
                                                        @Param("userId") int userId);

    List<FileDirectory> listFilesAndSubdirectoriesByDirectoryId(@Param("directoryId") int directoryId,
                                                                @Param("userId") int userId);

    void insertFileDirectory(FileDirectory fileDirectory);
    void markAsDeleted(@Param("fileDirectoryId") int fileDirectoryId, @Param("userId") int userId);
}
