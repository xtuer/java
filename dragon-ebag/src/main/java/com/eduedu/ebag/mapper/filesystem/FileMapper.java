package com.eduedu.ebag.mapper.filesystem;

import com.eduedu.ebag.bean.filesystem.File;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileMapper {
    /**
     * 查找用户的文件
     *
     * @param userId 用户的 id
     * @param fileId 文件的 id
     * @return File 对象
     */
    File selectFileByFileId(@Param("userId") int userId, @Param("fileId") int fileId);

    /**
     * 列出用户制定文件夹下的文件和子文件夹
     *
     * @param userId 用户的 id
     * @param directoryId 文件夹的 id
     * @return 文件和文件夹的 list
     */
    List<File> listFilesByDirectoryId(@Param("userId") int userId, @Param("directoryId") int directoryId);

    /**
     * 列出用户制定文件夹下子文件夹
     *
     * @param userId 用户的 id
     * @param directoryId 文件夹的 id
     * @return 子文件夹的 list
     */
    List<File> listSubdirectoriesByDirectoryId(@Param("userId") int userId, @Param("directoryId") int directoryId);

    /**
     * 列出用户制定文件夹下的文件
     *
     * @param userId 用户的 id
     * @param directoryId 文件夹的 id
     * @return 文件的 list
     */
    List<File> listFilesWithoutSubdirectoriesByDirectoryId(@Param("userId") int userId, @Param("directoryId") int directoryId);

    /**
     * 指定文件夹下文件和子文件夹的个数
     *
     * @param userId 用户的 id
     * @param directoryId 文件夹的 id
     * @return 文件和子文件夹的个数
     */
    int filesCountOfDirectory(@Param("userId") int userId, @Param("directoryId") int directoryId);

    /**
     * 查找用户下文件夹名字为传入的 directoryName 的文件夹的 id (同一个文件夹下有可能有多个同名的子文件夹)
     *
     * @param userId 用户的 id
     * @param directoryName 文件夹的名字
     * @param parentDirectoryId 父文件夹的 id
     * @return 文件夹的 id 的 list
     */
    List<Integer> selectDirectoryIdsByDirectoryName(@Param("userId") int userId,
                                                    @Param("directoryName") String directoryName,
                                                    @Param("parentDirectoryId") int parentDirectoryId);

    List<Integer> selectFileIdsByFileName(@Param("userId") int userId,
                                          @Param("fileName") String fileName,
                                          @Param("parentDirectoryId") int parentDirectoryId);

    /**
     * 插入文件到数据库
     * @param file 文件信息对象
     */
    void insertFile(File file);

    /**
     * 标记文件为被删除状态
     * @param userId 用户的 id
     * @param fileId 文件的 id
     * @return 受影响的行数
     */
    int markFileAsDeleted(@Param("userId") int userId, @Param("fileId") int fileId);

    /**
     * 移动文件到文件夹下
     *
     * @param userId 用户的 id
     * @param fileId 文件的 id
     * @param directoryId 文件夹的 id
     * @return
     */
    int moveFileToDirectory(@Param("userId") int userId, @Param("fileId") int fileId, @Param("directoryId") int directoryId);

    /**
     * 修改文件显示的名字
     * @param userId 用户的 id
     * @param fileId 文件的 id
     * @param displayName 新的文件名
     * @return
     */
    int renameFile(@Param("userId") int userId, @Param("fileId") int fileId, @Param("displayName") String displayName);

    /**
     * shared 为 true 时设置文件为共享, shared 为 false 时取消文件共享
     * @param userId 用户的 id
     * @param fileId 文件的 id
     * @param shared 为 true 时表示共享, 为 false 为不共享
     * @return
     */
    int shareFile(@Param("userId") int userId, @Param("fileId") int fileId, @Param("shared") boolean shared);
}
