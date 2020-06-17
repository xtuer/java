export default class ExamUtils {
    /**
     * 构建考试记录的序号 (服务器端已经按照 userId 排序了)
     *
     * @param {Array} examRecords 考试记录
     */
    static buildExamRecordsSn(examRecords) {
        for (let i = 0; i < examRecords.length; ++i) {
            let curr = examRecords[i];
            let next = examRecords[i+1];
            curr.sn = curr.sn || 1; // 不存在则为第一个

            if (next && curr.userId === next.userId) {
                next.sn = curr.sn + 1;
            }
        }
    }

    /**
     * 计算下一个题目的下标，如 { questionIndex: 2, subQuestionIndex: 1 } (复合题时才需要小题的下标 subQuestionIndex)
     *
     * @param {Array} questions      题目数组
     * @param {Int} questionIndex    题目的下标
     * @param {Int} subQuestionIndex 题目的小题的下标
     * @return {JSON} 返回下一个题目的下标
     */
    static nextQuestionIndexes(questions, questionIndex, subQuestionIndex) {
        // 1. 复合题
        //    1.1 subQuestionIndex 小于小题数-1，则为大题不变，小题为下一个小题
        //    1.2 subQuestionIndex 等于小题数-1，questionIndex < count-1，则为下一个一级题目
        // 2. 其他题: questionIndex < count-1，则为下一个一级题目

        const count    = questions.length; // 一级题目的个数
        const question = questions[questionIndex];

        // [1] 复合题
        if (question.type === QUESTION_TYPE.COMPOSITE) {
            const subCount = question.subQuestions.length; // 小题的个数

            // [1.1] subQuestionIndex 小于小题数-1，则为大题不变，小题为下一个小题
            if (subQuestionIndex < subCount-1) {
                subQuestionIndex += 1;
            // [1.2] subQuestionIndex 等于小题数-1，questionIndex < count-1，则为下一个一级题目
            } else if (subQuestionIndex === subCount-1 && questionIndex < count-1) {
                subQuestionIndex = 0;
                questionIndex   += 1;
            }
        // [2] 其他题: questionIndex < count-1，则为下一个一级题目
        } else if (questionIndex < count-1) {
            questionIndex += 1;
        }

        return { questionIndex, subQuestionIndex };
    }

    /**
     * 计算上一个题目的下标，如 { questionIndex: 2, subQuestionIndex: 1 } (复合题时才需要小题的下标 subQuestionIndex)
     *
     * @param {Array} questions      题目数组
     * @param {Int} questionIndex    题目的下标
     * @param {Int} subQuestionIndex 题目的小题的下标
     * @return {JSON} 返回上一个题目的下标
     */
    static prevQuestionIndexes(questions, questionIndex, subQuestionIndex) {
        // 1. 复合题
        //    1.1 subQuestionIndex 大于 0，则为大题不变，小题为上一个小题
        //    1.2 subQuestionIndex 等于 0，questionIndex > 0，则为上一个一级题目
        // 2. 其他题: questionIndex > 0，则为上一个一级题目

        const count    = questions.length; // 一级题目的个数
        const question = questions[questionIndex];

        // [1] 复合题
        if (question.type === QUESTION_TYPE.COMPOSITE) {
            // [1.1] subQuestionIndex 大于 0，则为大题不变，小题为上一个小题
            if (subQuestionIndex > 0) {
                subQuestionIndex -= 1;
            // [1.2] subQuestionIndex 等于 0，questionIndex > 0，则为上一个一级题目
            } else if (subQuestionIndex === 0 && questionIndex > 0) {
                subQuestionIndex = 0;
                questionIndex   -= 1;
            }
        // [2] 其他题: questionIndex > 0，则为上一个一级题目
        } else if (questionIndex > 0) {
            questionIndex -= 1;
        }

        return { questionIndex, subQuestionIndex };
    }

    /**
     * 是否有下一个题
     *
     * @param {Array} questions      题目数组
     * @param {Int} questionIndex    题目的下标
     * @param {Int} subQuestionIndex 题目的小题的下标
     * @return {JSON} 有下一个题返回 true，否则返回 false
     */
    static hasNextQuestion(questions, questionIndex, subQuestionIndex) {
        // 1. 复合题
        //    1.1 小题下标小于小题数-1，则返回 true
        //    1.2 大题下标小于一级题目数-1, 则返回 true
        // 2. 其他题: 下标小于一级题目数-1, 则返回 true
        // 3. 其他情况返回 false

        const question = questions[questionIndex];
        const count    = questions.length;

        // [1] 复合题
        if (question.type === QUESTION_TYPE.COMPOSITE) {
            const subCount = question.subQuestions.length;

            // [1.1] 小题下标小于小题数-1，则返回 true
            if (subQuestionIndex < subCount-1) {
                return true;
            // [1.2] 大题下标小于一级题目数-1, 则返回 true
            } else if (questionIndex < count-1) {
                return true;
            }
        // [2] 其他题: 下标小于一级题目数-1, 则返回 true
        } else if (questionIndex < count-1) {
            return true;
        }

        // [3] 其他情况返回 false
        return false;
    }

    /**
     * 是否有下一个题
     *
     * @param {Array} questions      题目数组
     * @param {Int} questionIndex    题目的下标
     * @param {Int} subQuestionIndex 题目的小题的下标
     * @return {JSON} 有下一个题返回 true，否则返回 false
     */
    static hasPrevQuestion(questions, questionIndex, subQuestionIndex) {
        // 1. 复合题
        //    1.1 小题下标大于 0，则返回 true
        //    1.2 大题下标大于 0, 则返回 true
        // 2. 其他题: 下标大于 0, 则返回 true
        // 3. 其他情况返回 false

        const question = questions[questionIndex];

        // [1] 复合题
        if (question.type === QUESTION_TYPE.COMPOSITE) {
            // [1.1] 小题下标大于 0，则返回 true
            if (subQuestionIndex > 0) {
                return true;
            // [1.2] 大题下标大于 0, 则返回 true
            } else if (questionIndex > 0) {
                return true;
            }
        // [2] 其他题: 下标大于 0, 则返回 true
        } else if (questionIndex > 0) {
            return true;
        }

        // [3] 其他情况返回 false
        return false;
    }

    /**
     * 判断传入的题目是否按整组题给分的题型题 (编辑试卷时使用)
     *
     * @param {JSON} question 题目
     * @return {Boolean} 整组给分的题型题返回 true，否则返回 false
     */
    static isScoreGroupQuestion(question) {
        // 在题型题上给分 (每题得分)，包括单选题、多选题、判断题、填空题
        return (question.type === QUESTION_TYPE.DESCRIPTION)
            && (question.purpose === QUESTION_TYPE.SINGLE_CHOICE || question.purpose === QUESTION_TYPE.MULTIPLE_CHOICE
                                                                 || question.purpose === QUESTION_TYPE.TFNG
                                                                 || question.purpose === QUESTION_TYPE.FITB
            );
    }

    /**
     * 统计同一个分组的题目数量
     *
     * @param {Array} questions 题目数组
     * @param {Int}   groupSn 分组序号
     */
    static countGroupQuestion(questions, groupSn) {
        return questions.filter(q => q.groupSn === groupSn).length;
    }

    /**
     * 判断题目是否客观题 (单选题、多选题、判断题)
     *
     * @param {JSON} question 题目
     * @return {Boolean} 客观题返回 true，否则返回 false
     */
    static isObjectiveQuestion(question) {
        if (question.type === QUESTION_TYPE.SINGLE_CHOICE
                || question.type === QUESTION_TYPE.MULTIPLE_CHOICE
                || question.type === QUESTION_TYPE.TFNG) {
            return true;
        }

        return false;
    }

    /**
     * 判断题目是否主观题 (填空题、问答题)
     *
     * @param {JSON} question 题目
     * @return {Boolean} 主观题返回 true，否则返回 false
     */
    static isSubjectiveQuestion(question) {
        if (question.type === QUESTION_TYPE.FITB || question.type === QUESTION_TYPE.ESSAY) {
            return true;
        }

        return false;
    }

    /**
     * 构建题目的作答
     *
     * @param {JSON} question 题目
     * @return {JSON} 返回题目的作答
     */
    static buildQuestionAnswer(question) {
        // 1. 构建作答的结构，参考 ExamDao.answerQuestions() 的说明
        // 2. 客观题: 单选题、多选题、判断题: 作答的选项为 checked 为 true 的选项
        // 3. 主观题: 填空题、问答题: 作答的选项为所有选项
        // 4. 返回题目的作答

        // [1] 构建作答的结构，参考 ExamDao.answerQuestions() 的说明
        const qa = {
            submitted: false,
            questions: [
                { questionId: question.id, answers: [] }
            ],
        };

        // [2] 客观题: 单选题、多选题、判断题: 作答的选项为 checked 为 true 的选项
        if (ExamUtils.isObjectiveQuestion(question)) {
            for (let option of question.options) {
                if (option.checked) {
                    qa.questions[0].answers.push({ questionOptionId: option.id, content: '' });
                }
            }
        // [3] 主观题: 填空题、问答题: 作答的选项为所有选项
        } else if (ExamUtils.isSubjectiveQuestion(question)) {
            for (let option of question.options) {
                qa.questions[0].answers.push({ questionOptionId: option.id, content: option.answer });
            }
        }

        // [4] 返回题目的作答
        return qa;
    }

    /**
     * 构建交卷的作答
     *
     * @param {Array} questions 题目数组
     * @return {JSON} 返回交卷的作答
     */
    static buildSubmitQuestionAnswers(questions) {
        // 1. 提取所有可作答的题目
        // 2. 构建所有客观题和主观题的回答

        const qas = { submitted: true, questions: [] }; // 交卷的作答

        // [1] 提取所有可作答的题目
        const qs = ExamUtils.answerableQuestions(questions);

        // [2] 构建所有客观题和主观题的回答
        qs.forEach(q => {
            const qa = ExamUtils.buildQuestionAnswer(q);
            qas.questions.push(...qa.questions);
        });

        return qas;
    }

    /**
     * 返回 questions 中所有可以作答的题目: 单选题、多选题、判断题、填空题、问答题、复合题的小题
     *
     * @param {Array} questions 题目数组
     * @return {Array} 可作答题目的数组
     */
    static answerableQuestions(questions) {
        const qs  = []; // 所有客观题和主观题

        questions.forEach(q => {
            if (ExamUtils.isObjectiveQuestion(q) || ExamUtils.isSubjectiveQuestion(q)) {
                qs.push(q);
            } else if (q.type === QUESTION_TYPE.COMPOSITE) {
                qs.push(...q.subQuestions);
            }
        });

        return qs;
    }
}
