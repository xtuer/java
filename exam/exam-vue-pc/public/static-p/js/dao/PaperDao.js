/**
 * 试卷的 Dao
 */
export default class PaperDao {
    /**
     * 查询当前机构的试卷
     *
     * 网址: http://localhost:8080/api/exam/papers/ofCurrentOrg
     * 参数: filter 中包含以下参数
     *     type      [可选]: 试卷类型: 0 (普通试卷)、1 (调查问卷)
     *     title     [可选]: 试卷标题，可模糊搜索
     *     pageSize  [可选]: 数量
     *     pageNumber[可选]: 页码
     *
     * @param {JSON} filter 过滤条件
     * @return {Promise} 返回 Promise 对象，resolve 的参数为试卷数组，reject 的参数为错误信息
     */
    static findPapersOfCurrentOrg(filter) {
        return new Promise((resolve, reject) => {
            Rest.get(Urls.API_PAPERS_OF_CURRENT_ORG, { data: filter }).then(({ data: papers, success, message }) => {
                if (success) {
                    resolve(papers);
                } else {
                    Notice.error({ title: '查询错误', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 查询指定 ID 的试卷
     *
     * 网址: http://localhost:8080/api/exam/papers/{paperId}
     * 参数: 无
     *
     * @param {Long} paperId 试卷 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为试卷，reject 的参数为错误信息
     */
    static findPaperById(paperId) {
        return new Promise((resolve, reject) => {
            Rest.get(Urls.API_PAPERS_BY_ID, { params: { paperId } }).then(({ data: paper, success, message }) => {
                if (success) {
                    resolve(paper);
                } else {
                    Notice.error({ title: '查询错误', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 更新创建试卷
     *
     * 网址: http://localhost:8080/api/exam/papers/{paperId}
     * 参数: 无
     * RequestBody 为 JSON 格式的试卷
     *
     * @param {JSON} paper 试卷
     * @return {Promise} 返回 Promise 对象，resolve 的参数为更新后的试卷，reject 的参数为错误信息
     */
    static upsertPaper(paper) {
        return new Promise((resolve, reject) => {
            Rest.update(Urls.API_PAPERS_BY_ID, {
                params: { paperId: paper.id },
                data  : paper,
                json  : true,
            }).then(({ data: newPaper, success, message }) => {
                if (success) {
                    resolve(newPaper);
                } else {
                    Notice.error({ title: '更新错误', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 删除试卷
     *
     * 网址: http://localhost:8080/api/exam/papers/{paperId}
     * 参数: 无
     *
     * @param {Long} paperId 试卷 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数无，reject 的参数为错误信息
     */
    static deletePaper(paperId) {
        return new Promise((resolve, reject) => {
            Rest.remove(Urls.API_PAPERS_BY_ID, { params: { paperId } }).then(({ success, message }) => {
                if (success) {
                    resolve();
                } else {
                    Notice.error({ title: '删除错误', desc: message });
                    reject(message);
                }
            });
        });
    }
}
