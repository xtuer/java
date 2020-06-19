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
                    Toast.fail(message);
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
     * @param userId 用户 ID
     * @param examId 考试 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为考试，reject 的参数为错误信息
     */
    static findExam(userId, examId) {
        return new Promise((resolve, reject) => {
            Rest.get(Urls.API_USER_EXAMS, { params: { userId, examId } }).then(({ data: exam, success, message }) => {
                if (success) {
                    resolve(exam);
                } else {
                    Toast.fail(message);
                    reject(message);
                }
            });
        });
    }
}
