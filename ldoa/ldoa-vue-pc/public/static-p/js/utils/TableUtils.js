import TableConfigDao from '@/../public/static-p/js/dao/TableConfigDao';

/**
 * 表格的工具类
 */
export default class TableUtils {
    /**
     * 恢复表格的列宽度
     *
     * 调用案例:
     *     1. 在 data() 中定义列 columns，可恢复宽度的列 resizable 为 true
     *        columns: [{ key: 'name', title: '名称', width: 200, resizable: true }]
     *     2. 在 mounted 中调用 this.restoreTableColumnWidths(this.tableName, this.currentUserId(), this.columns) 恢复列宽
     *
     * @param {String} tableName 表名
     * @param {Long}   userId    用户 ID
     * @param {Array}  columns   表格的列
     */
    static restoreTableColumnWidths(tableName, userId, columns) {
        // 1. 从 local storage 读取表格配置
        // 2. 配置已存在，则使用它进行恢复列宽
        // 2. 配置不存在，则从服务器加载配置，并保存到 local storage，恢复列宽

        // [1] 从 local storage 读取表格配置
        try {
            const config = JSON.parse(sessionStorage.getItem(tableName));

            if (config) {
                // [2] 配置已存在，则使用它进行恢复列宽
                TableUtils.doRestoreTableColumnWidths(columns, config);
                return;
            }
        } catch {
            // eslint-disable-next-line no-empty
        }

        console.log(`表 ${tableName} 的配置不存在或者无效，将从服务器加载`);

        // [2] 配置不存在，则从服务器加载配置，并保存到 local storage，恢复列宽
        TableConfigDao.findTableConfig(tableName, userId).then(configData => {
            const config = JSON.parse(configData.config);

            if (config) {
                sessionStorage.setItem(tableName, configData.config);
                TableUtils.doRestoreTableColumnWidths(columns, config);
            }
        }).catch((err) => {
            // 出错的情况，有可能 JSON 格式不对，没有数据
            console.error(err);
        });
    }

    /**
     * 恢复表格的列宽
     *
     * @param {Array} columns 表格的列
     * @param {Array} config  表格配置
     */
    static doRestoreTableColumnWidths(columns, config) {
        // 1. 遍历 columns，处理每一列的宽度
        // 2. 列的 resizable 为 false 则使用默认的列宽，不需要恢复
        // 3. 查找列的配置
        // 4. 配置中列宽存在时进行恢复

        // [1] 遍历 columns，处理每一列的宽度
        for (let i = 0; i < columns.length; i++) {
            const col = columns[i];

            // [2] 列的 resizable 为 false 则使用默认的列宽，不需要恢复
            if (!col.resizable) {
                continue;
            }

            // [3] 查找列的配置
            config.filter(c => c.index === i).forEach(c => {
                let width = parseInt(c.width);

                // [4] 配置中列宽存在时进行恢复
                if (width) {
                    // 限制列宽 (注意: 目前 iView Table 的 minWidth, maxWidth, width 不能同时生效)
                    const min = col.minWidth || 10;
                    const max = col.maxWidth || 500;
                    width = Utils.clamp(min, width, max);
                    col.width = width;
                }
            });
        }
    }

    /**
     * 保存列宽
     *
     * 调用案例: <Table :data="items" :columns="columns" @on-column-width-resize="saveTableColumnWidths(tableName, currentUserId(), ...arguments)">
     * 注意上面的 ...arguments 是为了获取 iView Table 回调的多个参数，是固定写法
     *
     * @param {String} tableName 表名
     * @param {Long}   userId    用户 ID
     * @param {Int}    newWidth  新的宽度
     * @param {Int}    oldWidth  旧的宽度
     * @param {Object} column    iView Table 组件的列对象
     */
    static saveTableColumnWidths(tableName, userId, newWidth, oldWidth, column) {
        // 1. 从 local storage 读取表格配置
        // 2. 查找对应列的配置，不存在则创建，更新对应列的宽度，然后保存到 local storage
        // 3. 保存表格配置到服务器
        console.log(tableName, newWidth, oldWidth, column._index);

        let config = [];

        // [1] 从 local storage 读取表格配置
        try {
            config = JSON.parse(sessionStorage.getItem(tableName));

            if (!config) {
                config = [];
            }
        } catch {
            // eslint-disable-next-line no-empty
        }

        // [2] 查找对应列的配置，不存在则创建，更新对应列的宽度，然后保存到 local storage
        let found = config.find(c => c.index === column._index);

        if (!found) {
            found = {};
            config.push(found);
        }

        found.index = column._index;
        found.width = parseInt(newWidth);

        // 保存到 local storage
        const configJson = JSON.stringify(config);
        sessionStorage.setItem(tableName, configJson);

        // [3] 保存表格配置到服务器
        // 提示: 不能使用 this.currentUserId()，因为这种调用方式 this 不是 Vue 实例
        TableConfigDao.upsertTableConfig(tableName, userId, configJson);
    }
}
