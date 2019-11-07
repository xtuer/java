import { Notice } from 'view-design';

/**
 * 访问试卷的 Dao
 */
export default class PaperDao {
    /**
     * 查询指定 ID 的试卷
     *
     * 网址: http://localhost:8080/api/papers/{paperId}
     * 参数: 无
     *
     * @param {Long} paperId 试卷 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为试卷，reject 的参数为错误信息
     */
    static findPaper(paperId) {
        return new Promise((resolve, reject) => {
            Rest.get({ url: Urls.API_PAPERS_BY_ID, pathVariables: { paperId } }).then(({ data: paper, success, message }) => {
                if (success) {
                    resolve(paper);
                } else {
                    Notice.error({ title: '查询试卷错误', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 查询当前机构的试卷
     *
     * 网址: http://localhost:8080/api/currentOrg/papers
     * 参数: filter 可包含下面几个属性
     *      title     [可选]: 试卷标题，可模糊搜索
     *      pageSize  [可选]: 数量
     *      pageNumber[可选]: 页码
     *
     * @param {JSON} filter 过滤条件
     * @return {Promise} 返回 Promise 对象，resolve 的参数为试卷数组，reject 的参数为错误信息
     */
    static findPapersOfCurrentOrg(filter) {
        return new Promise((resolve, reject) => {
            Rest.get({ url: Urls.API_PAPERS_OF_CURRENT_ORG, data: filter }).then(({ data: papers, success, message }) => {
                if (success) {
                    resolve(papers);
                } else {
                    Notice.error({ title: '查询试卷错误', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 更新创建试卷
     *
     * 网址: http://localhost:8080/api/papers/{paperId}
     * 参数: 无
     * RequestBody 为 JSON 格式的试卷
     *
     * @param {JSON} paper 试卷
     * @return {Promise} 返回 Promise 对象，resolve 的参数为试卷 ID，reject 的参数为错误信息
     */
    static upsertPaper(paper) {
        return new Promise((resolve, reject) => {
            Rest.update({ url: Urls.API_PAPERS_BY_ID, pathVariables: { paperId: paper.id }, data: paper, json: true })
                .then(({ data: paperId, success, message }) => {
                    if (success) {
                        Message.success('试卷保存成功');
                        resolve(paperId);
                    } else {
                        Message.error(message);
                        reject(message);
                    }
                });
        });
    }
}
