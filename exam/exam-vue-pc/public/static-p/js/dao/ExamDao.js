/**
 * 考试相关的 Dao
 */
export default class ExamDao {
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
     * @param filter 过滤条件
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
}
