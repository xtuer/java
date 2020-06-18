/**
 * 考试的 Dao
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

    /**
     * 创建或者更新考试
     *
     * 网址: http://localhost:8080/api/exam/exams/{examId}
     * 参数:
     *      title     [必选]: 考试标题
     *      paperIds  [必选]: 试卷 ID 数组
     *      startTime [必选]: 考试开始时间
     *      endTime   [必选]: 考试结束时间
     *      duration  [必选]: 考试时长
     *      maxTimes  [必选]: 最多允许考试次数
     *
     * @param {JSON} exam 考试对象
     * @return {Promise} 返回 Promise 对象，resolve 的参数为更新后的考试，reject 的参数为错误信息
     */
    static upsertExam(exam) {
        exam = JSON.parse(JSON.stringify(exam)); // 时间对象序列化

        return new Promise((resolve, reject) => {
            Rest.update(Urls.API_EXAMS_BY_ID, {
                params: { examId: exam.id },
                data  : exam,
                json  : true,
            }).then(({ data: newExam, success, message }) => {
                if (success) {
                    newExam.startTime = newExam.startTime.toDate();
                    newExam.endTime   = newExam.endTime.toDate();

                    resolve(newExam);
                } else {
                    Notice.error({ title: '保存错误', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 删除考试
     *
     * 网址: http://localhost:8080/api/exam/exams/{examId}
     * 参数: 无
     *
     * @param examId 考试 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数无，reject 的参数为错误信息
     */
    static deleteExam(examId) {
        return new Promise((resolve, reject) => {
            Rest.remove(Urls.API_EXAMS_BY_ID, { params: { examId } }).then(({ success, message }) => {
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
