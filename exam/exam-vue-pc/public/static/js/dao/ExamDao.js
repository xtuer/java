import { Message } from 'view-design';

/**
 * 访问考试的 Dao
 */
export default class ExamDao {
    /**
     * 查找当前机构的考试
     *
     * 网址: http://localhost:8080/api/exam/exams/ofCurrentOrg
     * 参数: 无
     *
     * @return {Promise} 返回 Promise 对象，resolve 的参数为考试的数组，reject 的参数为错误信息
     */
    static findExamsOfCurrentOrg() {
        return new Promise((resolve, reject) => {
            Rest.get({ url: Urls.API_EXAMS_OF_CURRENT_ORG }).then(({ data: exams, success, message }) => {
                if (success) {
                    resolve(exams);
                } else {
                    Notice.error({ title: '获取当前机构的考试错误', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 创建或者更新考试
     *
     * 网址: http://localhost:8080/api/exam/exams/ofCurrentOrg
     * 参数:
     *      title     [必选]: 考试标题
     *      paperId   [必选]: 试卷 ID
     *      startTime [必选]: 考试开始时间
     *      endTime   [必选]: 考试结束时间
     *      duration  [必选]: 考试时长
     *      maxTimes  [必选]: 最多允许考试次数
     *
     * @param {JSON} exam 考试
     * @return {Promise} 返回 Promise 对象，resolve 的参数为考试 ID，reject 的参数为错误信息
     */
    static upsertExam(exam) {
        exam = JSON.parse(JSON.stringify(exam)); // 处理 exam 中的 Date

        return new Promise((resolve, reject) => {
            Rest.update({ url: Urls.API_EXAMS_OF_CURRENT_ORG, data: exam }).then(({ data: examId, success, message }) => {
                if (success) {
                    Message.success('试卷保存成功');
                    resolve(examId);
                } else {
                    Notice.error({ title: '试卷保存错误', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 查找用户的考试信息，如果用户在此考试中进行过作答，同时查找出所有相关的考试记录
     *
     * 网址: http://localhost:8080/api/exam/users/{userId}/exams/{examId}
     * 参数: 无
     *
     * @param {Long} userId 用户 ID
     * @param {Long} examId 考试 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为考试对象，reject 的参数为错误信息
     */
    static findUserExam(userId, examId) {
        return new Promise((resolve, reject) => {
            Rest.get({ url: Urls.API_USER_EXAMS, pathVariables: { userId, examId } }).then(({ data: exam, success, message }) => {
                if (success) {
                    resolve(exam);
                } else {
                    Notice.error({ title: '获取当前机构的考试错误', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 创建用户某次考试的考试记录
     *
     * 网址: http://localhost:8080/api/exam/users/{userId}/exams/{examId}/records
     * 参数: 无
     *
     * @param {Long} userId 用户 ID
     * @param {Long} examId 考试 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为考试记录 ID，reject 的参数为错误信息
     */
    static insertExamRecord(userId, examId) {
        return new Promise((resolve, reject) => {
            Rest.create({ url: Urls.API_USER_EXAM_RECORDS, pathVariables: { userId, examId } }).then(({ data: examRecordId, success, message }) => {
                if (success) {
                    resolve(examRecordId);
                } else {
                    Notice.error({ title: '创建考试记录错误', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 查询用户的考试记录，内容有: 考试记录信息、考试的试卷、用户的作答
     *
     * 网址: http://localhost:8080/api/exam/users/{userId}/exams/{examId}/records/{recordId}
     * 参数: 无
     *
     * @param {Long} userId   用户 ID
     * @param {Long} examId   考试 ID
     * @param {Long} recordId 考试记录 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为考试记录，reject 的参数为错误信息
     */
    static findExamRecord(userId, examId, recordId) {
        return new Promise((resolve, reject) => {
            Rest.get({ url: Urls.API_USER_EXAM_RECORDS_BY_ID, pathVariables: { userId, examId, recordId } }).then(({ data: examRecord, success, message }) => {
                if (success) {
                    resolve(examRecord);
                } else {
                    Notice.error({ title: '查找考试记录错误', desc: message });
                    reject(message);
                }
            });
        });
    }

    /**
     * 对考试记录的题目进行作答
     *
     * 网址: http://localhost:8080/api/exam/users/{userId}/exams/{examId}/records/{recordId}/answer
     * 参数: 无
     * Request Body 为:
     * {
     *     "submitted": false,
     *     "answers": [
     *         { "questionId": 0, "questionOptionId": 0, "content": "" },
     *         { "questionId": 0, "questionOptionId": 0, "content": "" }
     *     ]
     * }
     *
     * @param userId {Long}   用户 ID
     * @param examId {Long}   考试 ID
     * @param recordId {Long} 考试记录 ID
     * @param examRecordAnswer {JSON} 回答
     * @return {Promise} 返回 Promise 对象，resolve 的参数为成功作答的选项 ID 的数组，reject 的参数为错误信息
     */
    static answerExamRecord(userId, examId, recordId, examRecordAnswer) {
        return new Promise((resolve, reject) => {
            Rest.create({ url: Urls.API_USER_EXAM_ANSWER_QUESTIONS, pathVariables: { userId, examId, recordId }, data: examRecordAnswer, json: true })
                .then(({ data: optionIds, success, message }) => {
                    if (success) {
                        resolve(optionIds);
                    } else {
                        Notice.error({ title: '回答考试错误', desc: message });
                        reject(message);
                    }
                });
        })
    }
}
