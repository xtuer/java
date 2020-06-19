/**
 * 考试相关的 Dao
 */
export default class ExamDao {
    /**
     * 查找当前机构的考试
     *
     * 网址: http://localhost:8080/api/exam/exams/ofCurrentOrg
     * 参数: 无
     *
     * @return {Promise} 返回 Promise 对象，resolve 的参数为考试，reject 的参数为错误信息
     */
    static findExamsOfCurrentOrg() {
        return new Promise((resolve, reject) => {
            Rest.get(Urls.API_EXAMS_OF_CURRENT_ORG).then(({ data: exams, success, message }) => {
                if (success) {
                    exams.forEach(exam => {
                        exam.startTime = exam.startTime.toDate();
                        exam.endTime   = exam.endTime.toDate();
                    });

                    resolve(exams);
                } else {
                    Notice.error({ title: '查询错误', desc: message });
                    reject(message);
                }
            });
        });
    }
}
