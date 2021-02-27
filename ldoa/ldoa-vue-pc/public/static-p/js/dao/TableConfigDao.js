/**
 * 表格配置的 Dao
 */
export default class TableCofigDao {
    /**
     * 查询指定表格的某个用户的配置
     *
     * 网址: http://localhost:8080/api/tables/{tableName}/users/{userId}/config
     * 参数: 无
     *
     * @param tableName 表名
     * @param userId    用户 ID
     * @return payload 为表格的配置
     */
    static findTableConfig(tableName, userId) {
        return Rest.get(Urls.API_TABLE_CONFIG_BY_TABLE_NAME_AND_USER, { params: { tableName, userId } }).then(({ data: config, success, message }) => {
            return Utils.response(config, success, message, false, false);
        });
    }

    /**
     * 更新或者插入表格的配置
     *
     * 网址: http://localhost:8080/api/tables/{tableName}/users/{userId}/config
     * 参数: config (必要): 配置
     *
     * @param config 表格的配置
     */
    static upsertTableConfig(tableName, userId, config) {
        return Rest.update(Urls.API_TABLE_CONFIG_BY_TABLE_NAME_AND_USER, { params: { tableName, userId }, data: { config } }).then(({ success, message }) => {
            return Utils.response(null, success, message);
        });
    }
}
