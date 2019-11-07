/**
 * 题目的工具类
 *
 * 提示:
 * 1. 题型: 单选题 (1)、多选题 (2)、判断题 (3)、填空题 (4)、问答题 (5)、复合题 (6)、题型题 (7)
 * 2. created 的用途:
 *    新创建的题目和选项提交到服务器前把他们的 ID 修改为 0，这是因为服务器在处理时发现 ID 为 0 表明是新建的，
 *    在保存到数据库前服务器会为它们生成一个全局唯一的 ID，插入到数据库，ID 非 0 则说明是已经存在的，则更新它们。
 *    在 appendQuestionOption() 中生成选项时给定了一个非 0 ID，这是因为 Vue 的 v-for 中遍历选项时
 *    使用 :key 绑定 ID，那么 ID 就不能为 0 了，所以就给新创建的题目和选项增加了一个标记 created 为 true，
 *    在提交时发现 created 为 true 就把它们的 ID 设置为 0。
 * 3. deleted 的用途:
 *    页面上删除题目或者选项时不是把它们从对应的数组里删除，而是设置他们的 deleted 为 true，界面上不显示它们，提交时:
 *    2.1 如果 created 为 true  (新创建的)，deleted 为 true，那么先把他们从数组里删除掉，不传给服务器
 *    2.2 如果 created 为 false (已存在的)，deleted 为 true，要传给服务器，服务器处理时从数据库里删除它们，ID 不为 0，服务器知道是已存在的
 * 4. 添加、删除题目的选项后，需要调用 updateOptionMarks() 更新一下选项的标记 A, B, C, D
 * 5. 在提交到服务器的时候，需要调用 cleanQuestion() 清理一下题目，删除其中 created 和 deleted 同时为 true 的项
 */
export default class QuestionUtils {
    /**
     * 创建题目，会自动给题目分配一个 ID
     *
     * @param  {Integer} type    题目的类型
     * @param  {String}  stem    题干
     * @param  {Integer} purpose 用途
     * @return {JSON} 返回新建题目的 JSON 对象
     */
    static createQuestion(type, stem = '请输入题干', purpose = 0) {
        // 1. 创建题目对象
        // 2. 如果是单选题或者多选题，则增加 4 个默认选项
        // 3. 如果是判断题，则增加 2 个默认选项
        // 4. 如果是问答题，则创建 1 个选项，用于存储问答题的答案

        // [1] 创建题目对象
        let question = {
            id: Utils.nextId(),
            stem,
            key       : '', // 参考答案
            analysis  : '', // 解析
            options   : [], // 选项
            type,           // 题型: 单选题 (1)、多选题 (2)、判断题 (3)、填空题 (4)、问答题 (5)、复合题 (6)、题型题 (7)
            purpose,        // 此题目的用途，例如题型题的 purpose 为此题型的 type
            groupSn   : 0,  // 题型分组序号 (试卷中使用)
            snLabel   : '', // 题目的序号: 一、1. (1)
            score     : 5,  // 分值 (1. 试卷中题型题的每题得分，2. 考试时此题得分)
            totalScore: 5,  // 满分 (1. 试卷中题型题的大题满分，2. 其他题每题满分)
            created   : true,  // 新创建的标记
            deleted   : false, // 删除标记
            parentId  : '0',   // 复合题的小题所属复合题的 ID
            subQuestions: [],  // 复合题的小题
        };

        if (type === QUESTION_TYPE.SINGLE_CHOICE || type === QUESTION_TYPE.MULTIPLE_CHOICE) {
            // [2] 如果是单选题或者多选题，则增加 4 个默认选项
            QuestionUtils.appendQuestionOption(question);
            QuestionUtils.appendQuestionOption(question);
            QuestionUtils.appendQuestionOption(question);
            QuestionUtils.appendQuestionOption(question);
        } else if (type === QUESTION_TYPE.TFNG) {
            // [3] 如果是判断题，则增加 2 个默认选项
            QuestionUtils.appendQuestionOption(question, '正确', false);
            QuestionUtils.appendQuestionOption(question, '错误', false);
        } else if (type === QUESTION_TYPE.ESSAY_QUESTION) {
            // [4] 如果是问答题，则创建 1 个选项，用于存储问答题的答案
            QuestionUtils.appendQuestionOption(question);
        }

        return question;
    }

    /**
     * 给题目添加一个新的选项
     *
     * @param  {JSON}    question    题目
     * @param  {String}  description 选项的描述
     * @param  {Boolean} correct     是否正确答案，默认值为 false
     * @return {JSON} 返回新增加的选项
     */
    static appendQuestionOption(question, description = '新建选项', correct = false) {
        const option = {
            id: Utils.nextId(),
            description,
            correct,
            position: 0,
            checked : false, // 客观题作答时是否选中该选项
            created : true,
            deleted : false,
            mark    : '', // 选择题选项标记: A, B, C, D
            questionId: question.id,
        };

        question.options.push(option);
        QuestionUtils.updateOptionMarks(question);

        return option;
    }

    /**
     * 删除题目
     * 只是修改题目的 deleted 为 true，标记为被删除
     *
     * @param  {JSON} question 要删除的题目
     * @return 无返回值
     */
    static deleteQuestion(question) {
        question.deleted = true;
    }

    /**
     * 删除题目指定下标的选项
     * 只是修改选项的 deleted 为 true，标记为被删除，没有真的从题目的选项数组里删除
     *
     * @param  {JSON}    question    题目
     * @param  {Integer} optionIndex 要删除的选项的下标
     * @return 无返回值
     */
    static deleteQuestionOption(question, optionIndex) {
        // 1. 标记选项为删除状态: deleted 为 true
        // 2. 更新选项前面的 A, B, C, D
        const option = question.options[optionIndex];

        if (option) {
            option.deleted = true;
            QuestionUtils.updateOptionMarks(question);
        }
    }

    /**
     * 给复合题增加一个新的小题
     *
     * @param  {JSON}    question        复合题
     * @param  {Integer} subQuestionType 小题的题型
     * @return {JSON} 返回新增加的小题
     */
    static appendSubQuestion(question, subQuestionType) {
        const subQuestion = QuestionUtils.createQuestion(subQuestionType);
        subQuestion.parentId = question.id;
        question.subQuestions.push(subQuestion);

        return subQuestion;
    }

    /**
     * 删除复合题指定下标的小题
     * 只是修改小题的 deleted 为 true，标记为被删除，没有真的从小题的数组里删除
     *
     * @param  {JSON}    question 复合题
     * @param  {Integer} subQuestionIndex 要删除的小题的下标
     * @return 无返回值
     */
    static deleteSubQuestion(question, subQuestionIndex) {
        const subQuestion = question.subQuestions[subQuestionIndex];

        if (subQuestion) {
            subQuestion.deleted = true; // 标记小题为删除状态: deleted 为 true
        }
    }

    /**
     * 删除整个题型题
     *
     * @param {Array} questions 题目数组
     * @param {JSON}  groupQuestion 题型题
     * @return 无返回值
     */
    static deleteGroupQuestions(questions, groupQuestion) {
        questions.filter(q => q.groupSn === groupQuestion.groupSn).forEach(q => {
            q.deleted = true;
        });
    }

    /**
     * 标记 option 为 question 的正确选项
     *
     * @param {JSON} question 题目
     * @param {JSON} option   选项
     * @return 无
     */
    static markCorrectOption(question, option) {
        // 1. 单选题、判断题: 只有一个正确选项
        //    1.1 设置所有选择的 correct 为 false
        //    1.2 设置 option.correct 为 true
        // 2. 多选题: 置反 option.correct

        if (question.type === QUESTION_TYPE.SINGLE_CHOICE || question.type === QUESTION_TYPE.TFNG) {
            // [1.1] 设置所有选择的 correct 为 false
            question.options.forEach(o => {
                o.correct = false;
            });

            // [1.2] 设置 option.correct 为 true
            option.correct = true;
        } else if (question.type === QUESTION_TYPE.MULTIPLE_CHOICE) {
            // [2] 多选题: 置反 option.correct
            option.correct = !option.correct;
        }
    }

    /**
     * 更新题目选项的标记为 A, B, C, D，例如添加或删除某个选项后就要重新计算一次
     * Mark 指的是选择题选项的标记 A, B, C, D
     *
     * @param  {JSON} question 题目
     * @return 无返回值
     */
    static updateOptionMarks(question) {
        // 1. 更新选项的标记: 把题目选项中 deleted 为 false 的选项按顺序设置编号 A, B, C, D
        // 2. 更新小题选项的标记: 使用递归

        // [1] 更新选项的标记
        let sn = 0;
        question.options.filter(o => !o.deleted).forEach(o => {
            o.mark = String.fromCharCode(65 + sn); // 65 是 A
            sn += 1;
        });

        // [2] 更新小题选项的标记: 使用递归
        question.subQuestions.forEach(q => {
            QuestionUtils.updateOptionMarks(q);
        });
    }

    /**
     * 更新题目的序号
     *
     * @param {Array} questions 题目数组
     * @return 无
     */
    static updateQuestionSnLabels(questions) {
        // 1. 题型题: 使用中文序号，如 二、
        // 2. 普通题: 使用阿拉伯数字，如 2、
        // 3. 复合题的小题: 使用阿拉伯数字加括号，如（2）

        let gsn = 0; // 题型题
        let qsn = 0; // 普通题
        let ssn = 0; // 复合题的小题
        let groupSn = -1; // 当前分组序号

        questions.filter(question => !question.deleted).forEach(question => {
            if (question.groupSn !== groupSn) {
                groupSn = question.groupSn;
                gsn += 1; // 新题型开始
            }

            // [1] 题型题
            if (question.type === QUESTION_TYPE.DESCRIPTION) {
                question.snLabel = Utils.toCnNumber(gsn) + '、';
                return;
            }

            // [2] 普通题
            qsn += 1;
            question.snLabel = qsn + '、';

            // [3] 复合题的小题
            ssn = 0;
            question.subQuestions.filter(sub => !sub.deleted).forEach(sub => {
                ssn += 1;
                sub.snLabel = `(${ssn})　`;
            });
        });
    }

    /**
     * 添加题目到题型题 groupQuestion 所属题目组
     *
     * @param {Array} questions 题目
     * @param {JSON} groupQuestion 题型题
     * @return 无
     */
    static appendQuestionToGroup(questions, groupQuestion) {
        // 1. 找到最后一个 groupSn 与 groupQuestion.groupSn 相同的题目的下标 pos
        // 2. 在 pos + 1 处插入一个题目，题目的类型为 groupQuestion.purpose

        let pos = -1;
        for (let i = questions.length - 1; i >= 0; i--) {
            if (questions[i].groupSn === groupQuestion.groupSn) {
                pos = i;
                break;
            }
        }

        pos += 1;
        const type = groupQuestion.purpose;
        const question = QuestionUtils.createQuestion(type, '新创建的题目');
        question.groupSn = groupQuestion.groupSn;
        questions.splice(pos, 0, question);
    }

    /**
     * 更新题目的分数
     *
     * @param  {Array} questions 题目数组
     * @return {Integer} 返回题目的总分
     */
    static updateQuestionScores(questions) {
        // 1. 计算复合题的满分，为小题满分之和
        // 2. 按照题型计算满分
        // 3. 计算总分

        // [1] 计算复合题的满分，为小题满分之和
        questions.filter(q => !q.deleted && q.type === QUESTION_TYPE.COMPLEX).forEach(q => {
            q.totalScore = 0;
            q.subQuestions.filter(sub => !sub.deleted).forEach(sub => {
                q.totalScore += sub.totalScore;
            });
        });

        // [2] 按照题型计算满分
        questions.filter(q => !q.deleted && q.type === QUESTION_TYPE.DESCRIPTION).forEach(q => {
            QuestionUtils.updateGroupQuestionScores(questions, q);
        });

        // [3] 计算总分
        let totalScore = 0;
        questions.filter(q => !q.deleted && q.type !== QUESTION_TYPE.DESCRIPTION).forEach(q => {
            totalScore += q.totalScore;
        });

        return totalScore;
    }

    /**
     * 更新 groupQuestion 所在整个题型的题目的分数
     *
     * @param {Array} questions    题目数组
     * @param {JSON} groupQuestion 题型题
     * @return 无
     */
    static updateGroupQuestionScores(questions, groupQuestion) {
        // 1. 过滤掉被删除的题目、非本题型的题目和题型题自己
        // 2. 如果属于按组给分的题型，则普通题的满分为 groupQuestion.score
        // 3. 题型题的满分为所有普通题的满分之和

        groupQuestion.totalScore = 0;

        // [1] 过滤掉被删除的题目、非本题型的题目和题型题自己
        questions.filter(q => !q.deleted && q.id !== groupQuestion.id && q.groupSn === groupQuestion.groupSn).forEach(q => {
            // [2] 如果属于按组给分的题型，则普通题的满分为 groupQuestion.score
            if (QuestionUtils.isScoreGroupQuestion(groupQuestion)) {
                q.totalScore = groupQuestion.score;
            }

            // [3] 题型题的满分为所有普通题的满分之和
            groupQuestion.totalScore += q.totalScore;
        });
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
     * 判断传入的题目是否按题给分的题 (编辑试卷时使用)
     *
     * @param {JSON} question 题目
     * @return {Boolean} 按题给分的题返回 true，否则返回 false
     */
    static isScoreSelfQuestion(question) {
        // 复合题的小题或问答题
        return Utils.idIdValid(question.parentId) || question.type === QUESTION_TYPE.ESSAY_QUESTION;
    }

    /**
     * 整理题目，去掉不需要的题目和选项，设置新创建的题目和选项的 ID 为 0
     *
     * @param  {Array} questions 题目数组
     * @return {Array} 返回整理好后的题目数组
     */
    static cleanQuestions(questions) {
        // 1. 处理填空题: 解析填空题的题干，每个空生成一个 option
        // 2. 去掉 created === true && deleted === true 的题目
        // 3. 设置 created === true 的题目的 ID 为 0
        // 4. 去掉 created === true && deleted === true 的选项
        // 5. 设置 created === true 的选项的 ID 为 0
        // 6. 整理复合题的小题

        // [1] 处理填空题: 解析填空题的题干，每个空生成一个 option
        questions.filter(q => q.type === QUESTION_TYPE.FITB).forEach(q => {
            QuestionUtils.cleanFitbQuestion(q);
        });

        // [2] 去掉 created === true && deleted === true 的题目
        questions = questions.filter(q => !(q.created && q.deleted)).map(q => {
            // [2] 设置 created === true 的题目的 ID 为 0
            if (q.created) {
                q.id = 0;
            }

            // [3] 整理填空题

            // [4] 去掉 created === true && deleted === true 的选项
            q.options = q.options.filter(o => !(o.created && o.deleted)).map(o => {
                // [5] 设置 created === true 的选项的 ID 为 0
                if (o.created) {
                    o.id = 0;
                }

                return o;
            });

            // [6] 整理复合题的小题
            if (q.type === QUESTION_TYPE.COMPLEX) {
                q.subQuestions = QuestionUtils.cleanQuestions(q.subQuestions);
            }

            return q;
        });

        return questions;
    }

    /**
     * 处理填空题: 解析填空题的题干，每个空生成一个 option
     *
     * @param {JSON} question 填空题
     * @return 无
     */
    static cleanFitbQuestion(question) {
        // 1. 解析题干，得到空的数量 blankCount
        // 2. 如果 blankCount 小于题目的 options.length，则删除多余的 options
        //    2.1 前 blankCount 个选项保留
        //    2.2 后 (optionCount - blankCount) 个选项删除
        // 3. 如果 blankCount 大于题目的 options.length，则添加对应数量的 options

        // [1] 解析题干，得到空的数量 blankCount
        const div = document.createElement('div');
        div.innerHTML = question.stem;
        const blankCount = div.querySelectorAll('abbr').length;
        const optionCount = question.options.length;

        if (blankCount < optionCount) {
            // [2] 如果 blankCount 小于题目的 options.length，则删除多余的 options

            // [2.1] 前 blankCount 个选项保留
            for (let i = 0; i < optionCount; i++) {
                question.options[i].deleted = false;
            }

            // [2.2] 后 (optionCount - blankCount) 个选项删除
            for (let i = blankCount; i < optionCount; i++) {
                question.options[i].deleted = true;
            }
        } else if (blankCount > optionCount) {
            // [3] 如果 blankCount 大于题目的 options.length，则添加对应数量的 options
            for (let i = optionCount; i < blankCount; i++) {
                QuestionUtils.appendQuestionOption(question, '填空题的空');
            }
        }
    }

    /**
     * 计算下一个题型的 groupSn
     *
     * @param {Array} questions 题目数组
     * @return {Integer} 返回下一个 groupSn
     */
    static nextGroupSn(questions) {
        // 下一个 groupSn 为最后一个题型题的 groupSn+1
        const groupQuestions = questions.filter(q => q.type === QUESTION_TYPE.DESCRIPTION);
        const len = groupQuestions.length;

        return len > 0 ? groupQuestions[len-1].groupSn+1 : 0;
    }
}
