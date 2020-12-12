export default class DiskDao {
    /**
     * 查询用户 的文件，如果 userId 为 0，则查询所有用户的文件
     *
     * 网址: http://localhost:8080/api/disk/files
     * 参数:
     *      userId     [可选]: 用户 ID
     *      pageNumber [可选]: 页码
     *      pageSize   [可选]: 数量
     *
     * @param {JSON} filter 过滤条件，参考上面的参数
     * @return {Promise} 返回 Promise 对象，resolve 的参数为文件的数组，reject 的参数为错误信息
     */
    static findDiskFiles(filter) {
        return Rest.get(Urls.API_DISK_FILES, { data: filter }).then(({ data: files, success, message }) => {
            return Utils.response(files, success, message);
        });
    }

    /**
     * 保存文件到网盘
     *
     * 网址: http://localhost:8080/api/disk/files
     * 参数: fileId (必要): 文件 ID
     *
     * @param {Long} fileId 文件 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为网盘的文件，reject 的参数为错误信息
     */
    static saveFileToDisk(fileId) {
        return Rest.create(Urls.API_DISK_FILES, { data: { fileId } }).then(({ data: file, success, message }) => {
            return Utils.response(file, success, message);
        });
    }

    /**
     * 删除网盘中的文件
     *
     * 网址: http://localhost:8080/api/disk/files/{fileId}
     * 参数: 无
     *
     * @param {Long} fileId 文件 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static deleteDiskFile(fileId) {
        return Rest.del(Urls.API_DISK_FILES_BY_ID, { params: { fileId } }).then(({ success, message }) => {
            return Utils.response(null, success, message);
        });
    }
}
