<!--
功能: 编辑试卷使用的题目组件
已经使用的地方: 试卷编辑中的只读题目、可编辑题目、整卷批改里的题目

参数:
question  : [必选] 题目
toolbar   : [可选] 是否显示工具栏
correct   : [可选] 是否批改模式
readonly  : [可选] 只读模式，默认为 true
score-show: [可选] 是否显示分数
score-edit: [可选] 是否可修改分数 (score-show 时才会生效)

事件:
on-append-sub-question() : 添加小题时触发，参数无
on-delete-sub-question() : 删除小题时触发，参数无
on-score-change(question): 题目的满分变化时触发，参数为题目

on-append-question-to-group-click(groupQuestion): 点击添加题目到题型中的按钮，参数题型题 question
on-edit-question-click(question)  : 点击编辑题目按钮，参数 question
on-delete-question-click(question): 点击删除题目按钮，参数 question
on-move-down-click(question)      : 点击向下移动题目按钮，参数 question
on-move-up-click(question)        : 点击向上移动题目按钮，参数 question

示例:
<Question v-for="question in questions" :key="question.id" :question="question"
          readonly toolbar score-show score-edit
          @on-append-question-to-group-click="appendQuestionToGroup"
          @on-delete-question-click="deleteQuestion"
          @on-edit-question-click="editQuestion"
          @on-score-change="updateQuestionStatus"/>

只读题目的布局:
question
    stem
    option
        mark, description
    tfngs
        tfng
    key
    analysis
-->
<template>
    <div :id="`question-${question.id}`" :class="questionClass">
        <template v-if="readonly">
            <!--
                A. 题型题: 题干 (题干，分数，)
                B. 选择题: 题干、选项、答案、解析
                C. 判断题: 题干、选项、答案、解析
                D. 填空题、问答题: 题干、答案、解析
                E. 复合题: 题干，递归显示小题

                题干在编辑试卷和普通模式下不同
                编辑模式:
                    题型题显示每题得分、满分、添加按钮
                    普通题显示题干、删除按钮、编辑按钮
                普通模式:
                    题型题显示每题得分、满分
                    普通题显示题干
            -->
            <!-- [1] 题干 -->
            <div class="stem">
                <!-- 题目序号 + 题干 + 分数: 一、单选题，每题 5 分，共 10 分 -->
                <div>{{ question.snLabel }}</div>
                <div class="stem-content">
                    <div v-html="question.stem"></div>

                    <!-- 显示分数: 编辑分数或只读分数 -->
                    <template v-if="scoreShow">
                        <!-- 每题得分都一样 -->
                        <div v-if="scoreGroup && scoreEdit" class="group-score-edit">
                            ，每题 <InputNumber v-model="question.score" :min="1" :step="0.5" size="small" @on-change="$emit('on-score-change', question)"/> 分
                        </div>
                        <div v-else-if="scoreGroup && question.type===7">
                            ，每题 {{ question.score }} 分
                        </div>

                        <!-- 每题得分不一样 -->
                        <div v-if="scoreSelf && scoreEdit">
                            <InputNumber v-model="question.totalScore" :min="1" :step="0.5" size="small" style="margin-left: 0" @on-change="$emit('on-score-change', question)"/> 分
                        </div>
                        <div v-else-if="scoreSelf">
                            &nbsp;({{ question.totalScore }} 分)
                        </div>

                        <!-- 总分: 题型题 (非复合题的题型题) -->
                        <div v-if="question.type===7 && question.purpose!==6">
                            ，共 {{ question.totalScore }} 分
                        </div>
                    </template>
                </div>

                <!-- 批改题目时显示得分 (提示: 不放在题目上是因为题目的 position 为 relative 时复合题的小题滚动有问题) -->
                <Score v-if="correct && isAnswerable"
                        v-model="question.score"
                        :max="question.totalScore"
                        :editable="isSubjectiveQuestion"
                        @on-change="$emit('on-score-change', question)"/>
            </div>

            <!-- [2] 选择题: 选项 -->
            <template v-if="question.type===1 || question.type===2">
                <div v-for="option in options" :key="option.id" class="option" :class="optionClass(option)">
                    <div class="mark">{{ option.mark }}</div>
                    <div class="description" v-html="option.description"></div>
                </div>
            </template>

            <!-- [3] 判断题: 选项 (正确、错误) -->
            <template v-if="question.type===3">
                <div v-for="(option, index) in question.options" :key="option.id" class="option tfng" :class="optionClass(option)">
                    <Icon :type="tfngIconName(option, index)"/>{{ option.description }}
                </div>
            </template>

            <!-- [4] 星级题 -->
            <template v-if="question.type===8">
                <Rate :value="2"/>
            </template>

            <!-- [6] 题目解析 (可作答题目才显示) -->
            <QuestionAnalysis v-if="isAnswerable && analysisShow" :question="question" :correct="correct"/>

            <!-- [7] 工具栏 -->
            <QuestionToolbar v-if="toolbar" :question="question"
                             @on-append-question-to-group-click="$emit('on-append-question-to-group-click', question)"
                             @on-edit-question-click="$emit('on-edit-question-click', question)"
                             @on-delete-question-click="$emit('on-delete-question-click', question)"
                             @on-move-down-click="$emit('on-move-down-click', question)"
                             @on-move-up-click="$emit('on-move-up-click', question)"/>

            <!-- [8] 复合题: 递归显示小题 -->
            <template v-if="question.type===6">
                <Question v-for="subQuestion in subQuestions"
                          :key="subQuestion.id"
                          :question="subQuestion"
                          :readonly="readonly"
                          :correct="correct"
                          :score-edit="scoreEdit"
                          :score-show="scoreShow"
                          :analysis-show="analysisShow"
                          @on-score-change="$emit('on-score-change', subQuestion)"/>
            </template>
        </template>

        <!-------------------------------------------------------------------------------------------------------------
                                                             题目编辑
        -------------------------------------------------------------------------------------------------------------->
        <template v-else>
            <!-- 复合题 -->
            <template v-if="question.type===6">
                <!-- 1. 复合题: 题干，递归显示小题 -->
                <Tabs v-model="activeTabName" :animated="false" type="card" :before-remove="confirmDeleteSubQuestion" @on-tab-remove="deleteSubQuestion">
                    <!-- 复合题大题: 只有题干 -->
                    <TabPane label="复合题" name="big">
                        <Richtext v-model="question.stem" :min-height="150" inline placeholder="请输入复合题的题干"/>
                    </TabPane>

                    <!-- 复合题小题: 递归显示小题 -->
                    <TabPane v-for="subQuestion in subQuestions" :key="subQuestion.id" :label="questionTypeName(subQuestion.type)" :name="subQuestion.id" closable>
                        <Question :question="subQuestion"/>
                    </TabPane>

                    <!-- 添加小题下拉菜单 -->
                    <Dropdown slot="extra" transfer @on-click="appendSubQuestion">
                        <Button type="dashed" size="small" icon="md-add">添加小题</Button>
                        <DropdownMenu slot="list">
                            <DropdownItem v-for="type in subQuestionTypes" :key="type.value" :name="type.value">{{ type.name }}</DropdownItem>
                        </DropdownMenu>
                    </Dropdown>
                </Tabs>
            </template>

            <!-- 题型题: 题干 -->
            <template v-else-if="question.type===7">
                <Richtext v-model="question.stem" :min-height="150" inline placeholder="请输入题干"/>
            </template>

            <!-- 其他题: 选择题、判断题、填空题、问答题 -->
            <template v-else>
                <!--
                    3. 选择题: 题干、选项、解析
                    4. 判断题: 题干、选项、解析
                    5. 填空题、问答题: 题干、答案、解析
                    6. 星级题
                -->

                <!-- [2] 题型题: 题干 ([5] 填空题、问答题) -->
                <Richtext v-model="question.stem" :min-height="150" inline placeholder="请输入题干"/>

                <!-- [3] 选择题: 题干、选项、答案、解析 -->
                <template v-if="question.type===1 || question.type===2">
                    <div v-for="option in options" :key="option.id" class="option" :class="optionClass(option)">
                        <div class="mark" @click="markCorrectOption(option)">{{ option.mark }}</div>
                        <Richtext v-model="option.description" inline class="description" placeholder="请输入选项"/>
                        <Icon type="md-close" size="18" class="close clickable" @click="deleteOption(option)"/>
                    </div>
                    <Button type="dashed" size="small" icon="md-add" class="append-option-button" @click="appendOption">添加选项</Button>
                </template>

                <!-- [4] 判断题: 选项 (正确、错误) -->
                <template v-if="question.type===3">
                    <div v-for="(option, index) in question.options" :key="option.id"
                            class="option tfng" :class="optionClass(option)"
                            @click="markCorrectOption(option)">
                        <Icon :type="tfngIconName(option, index)"/>{{ option.description }}
                    </div>
                </template>

                <!-- [6] 星级题 -->
                <template v-if="question.type===8">
                    <Rate :value="2" disabled/>
                </template>

                <!-- 答案 (填空题和问答题才需要答案) -->
                <div v-if="analysisShow && (question.type===4 || question.type===5)" class="key">
                    <div>答案:</div>
                    <Richtext v-model="question.key" :min-height="150" inline placeholder="请输入题目的参考答案"/>
                </div>

                <!-- 解析 (题型题、星级题不需要) -->
                <div v-if="analysisShow" class="analysis">
                    <div>解析:</div>
                    <Richtext v-model="question.analysis" :min-height="150" inline placeholder="请输入题目的解析"/>
                </div>
            </template>
        </template>
    </div>
</template>

<script>
import QuestionUtils from '@/../public/static-p/js/util/QuestionUtils';

import Richtext from '@/components/Richtext.vue';
import Score from '@/components/Score.vue';
import QuestionToolbar from '@/components/QuestionToolbar.vue';
import QuestionAnalysis from '@/components/QuestionAnalysis.vue';

export default {
    name: 'Question', // 递归组件需要设置 name 属性，才能在模板中调用自己
    props: {
        question : { type: Object, required: true  }, // 题目
        toolbar  : { type: Boolean, default: false }, // 是否显示工具栏
        correct  : { type: Boolean, defualt: false }, // 是否批改模式
        readonly : { type: Boolean, default: false }, // 只读模式
        scoreShow: { type: Boolean, default: false }, // 显示分数
        scoreEdit: { type: Boolean, default: false }, // 编辑分数
        analysisShow: { type: Boolean, default: true }, // 是否显示题目解析
    },
    components: { Richtext, Score, QuestionToolbar, QuestionAnalysis },
    data() {
        return {
            activeTabName: 'big',
        };
    },
    methods: {
        // 添加选项
        appendOption() {
            QuestionUtils.appendQuestionOption(this.question);
        },
        // 删除选项
        deleteOption(option) {
            // 找到 ID 等于 option.id 的选项，删除它
            for (let i = 0; i < this.question.options.length; ++i) {
                if (this.question.options[i].id === option.id) {
                    QuestionUtils.deleteQuestionOption(this.question, i);
                    break;
                }
            }
        },
        // 标记 option 为题目的正确选项
        markCorrectOption(option) {
            // [Fix] 很丑陋的解决不允许问卷点击选项
            if (!this.analysisShow) {
                return;
            }

            QuestionUtils.markCorrectOption(this.question, option);
        },
        // 给复合题添加小题
        appendSubQuestion(type) {
            const sub = QuestionUtils.appendSubQuestion(this.question, type);
            this.activeTabName = sub.id; // 新添加的小题得到焦点
            this.$emit('on-append-sub-question');
        },
        // 删除复合题的小题
        deleteSubQuestion(subQuestionId) {
            // 找到 ID 等于 subQuestionId 的小题，删除它
            for (let i = 0; i < this.question.subQuestions.length; ++i) {
                if (this.question.subQuestions[i].id === subQuestionId) {
                    QuestionUtils.deleteSubQuestion(this.question, i);
                    this.$emit('on-delete-sub-question');
                    break;
                }
            }
        },
        // 确定是否删除复合题的小题
        confirmDeleteSubQuestion(index) {
            const subQuestions = this.question.subQuestions.filter(q => !q.deleted);
            const subQuestion  = subQuestions[index - 1];
            const type         = this.questionTypeName(subQuestion.type);

            return new Promise((resolve, reject) => {
                this.$Modal.confirm({
                    title: `确定删除 <font color="red">${type}</font> 吗?`,
                    loading: true,
                    onOk: () => {
                        resolve();
                        this.$Modal.remove();
                    }
                });
            });
        },
        // 恢复当前 Tab 为题干的 Tab
        resetActiveTabName() {
            this.activeTabName = 'big';
        },
        // 选项的样式
        optionClass(option) {
            return {
                checked: option.checked,
                correct: option.correct,
                right  : option.checked && option.correct,  // 回答正确: 选择且是正确答案
                wrong  : option.checked && !option.correct, // 回答错误: 选择但非正确答案
                single : this.question.type === 1, // 单选题
                multi  : this.question.type === 2, // 多选题
            };
        },
        // 判断题选项的图标名字
        tfngIconName(option, index) {
            // 区分批改和非批改
            if (this.correct && index === 0) {
                return option.checked ? 'ios-checkmark-circle' : 'ios-checkmark-circle-outline';
            } else if (this.correct && index === 1) {
                return option.checked ? 'ios-close-circle' : 'ios-close-circle-outline';
            } else if (!this.correct && index === 0) {
                return option.correct ? 'ios-checkmark-circle' : 'ios-checkmark-circle-outline';
            } else if (!this.correct && index === 1) {
                return option.correct ? 'ios-close-circle' : 'ios-close-circle-outline';
            }

            return '';
        }
    },
    computed: {
        // 题目的样式
        questionClass() {
            return {
                'question'          : true,
                'question-group'    : this.question.type === QUESTION_TYPE.DESCRIPTION,     // 题型题，题型分组
                'question-composite': this.question.type === QUESTION_TYPE.COMPOSITE,       // 复合题
                'question-multiple' : this.question.type === QUESTION_TYPE.MULTIPLE_CHOICE, // 多选题
                'question-sub'      : Utils.isValidId(this.question.parentId),              // 复合题的小题
                'question-editable' : !this.readonly,
                'question-readonly' : this.readonly,
                'question-correct'  : this.correct,
                'question-with-toolbar': this.toolbar,
                'question-answerable'  : QuestionUtils.isAnswerableQuestion(this.question),
            };
        },
        // 可用的小题，去掉被删除的小题
        subQuestions() {
            return this.question.subQuestions.filter(q => !q.deleted);
        },
        // 可用的选项，去掉被删除的选项
        options() {
            return this.question.options.filter(o => !o.deleted);
        },
        // 复合题小题的类型
        subQuestionTypes() {
            return QUESTION_TYPES.filter(q => q.value < window.QUESTION_TYPE.COMPOSITE);
        },
        // 给小组打分的题型: 每题得分都一样
        scoreGroup() {
            // 在题型题上给分 (每题得分)，包括单选题、多选题、判断题、填空题
            return QuestionUtils.isScoreGroupQuestion(this.question);
        },
        // 给自己打分的题型: 每题得分不一样
        scoreSelf() {
            // 复合题的小题、问答题
            return QuestionUtils.isScoreSelfQuestion(this.question);
        },
        // 可作答题目返回 true，否则返回 false
        isAnswerable() {
            return QuestionUtils.isAnswerableQuestion(this.question);
        },
        // 是否主客观题
        isSubjectiveQuestion() {
            return QuestionUtils.isSubjectiveQuestion(this.question);
        },
    }
};
</script>

<style lang="scss">
.question {
    display: grid;
    grid-gap: 6px;
    cursor: default;

    // 选项
    .option {
        // 标记: A, B, C, D
        .mark {
            @include alignCenter;
            border: 1px solid rgb(211, 211, 211);
            border-radius: 100%;
            transition: all .6s;
        }

        // 判断题
        &.tfng {
            display: flex;
            align-items: center;

            .ivu-icon {
                font-size: 28px;
                margin-right: 4px;
            }
        }
    }

    .option.correct {
        .mark {
            color: white;
            background: $lightPrimaryColor;
            border-color: $lightPrimaryColor;
        }

        .ivu-icon {
            color: $lightPrimaryColor;
        }
    }
}

// 只读题目
.question-readonly {
    border: 1px dashed transparent;
    border-radius: 4px;

    // 分组题的 div 和 p 不换行
    &.question-group .stem .stem-content {
        > div, > div > p {
            display: inline-block;
        }
    }

    // 题干
    .stem {
        display: grid;
        grid-template-columns: max-content 1fr;
        align-items: start;
        position: relative;

        // 题型题的分值样式
        .group-score-edit {
            display: flex;
            align-items: flex-start;
        }

        .ivu-input-number {
            width: 55px;
            margin: auto 6px;
        }
    }

    // 选择题选项
    .option {
        display: grid;
        grid-template-columns: max-content 1fr;
        grid-gap: 8px;

        // 选项的标记: A, B, C, D
        .mark {
            width : 26px;
            height: 26px;
        }

        .description {
            padding-top: 3px;
        }
    }

    &.question-group .stem {
        font-size: 18px;
        font-weight: bold;
    }

    &.question-multiple .mark {
        border-radius: 3px;
    }
}

// 显示工具栏时，鼠标移动到题目上，高亮它
.question-with-toolbar {
    position: relative;

    &:hover {
        border-radius: 3px 0 3px 3px;
        border: 1px solid $lightPrimaryColor;
        transition: all 1s;

        .question-toolbar {
            transition: all 1s;
            opacity: 1;
        }
    }
}

// 批改试卷时的题目样式
.question-correct {
    margin-bottom: 12px;

    &.question-answerable {
        border: 1px solid rgba(0, 0, 0, 0.05);
        padding: 12px;
    }

    &.question-sub {
        margin-bottom: 6px;
    }

    .option {
        // 选择题的 mark
        &.correct .mark {
            color: inherit;
            background: white;
            border-color: rgb(211, 211, 211);
        }
        &.checked.wrong .mark {
            color: white;
            border-color: $errorColor;
            background-color: $errorColor;
        }
        &.checked.right .mark {
            color: white;
            border-color: $successColor;
            background-color: $successColor;
        }

        // 判断题的图标
        &.correct .ivu-icon {
            color: inherit;
        }
        &.checked.wrong .ivu-icon {
            color: $errorColor;
        }
        &.checked.right .ivu-icon {
            color: $successColor;
        }
    }

    .score {
        position: absolute;
        top  : -10px;
        right: -5px;
        font-size: 18px;

        img {
            width : 30px;
            height: 30px;
        }
    }
}

// 编辑题目
.question-editable {
    // 选择题选项
    .option {
        display: grid;
        grid-template-columns: 30px 1fr;
        position: relative;

        .mark {
            border-right: none;
            border-radius: 4px 0 0 4px;
            cursor: pointer;
        }

        .richtext .mce-content-body {
            border-radius: 0 4px 4px 0;
        }

        // 删除选项按钮
        .close {
            position: absolute;
            top  : 8px;
            right: 8px;
            color: #bbb;
        }

        // 判断题的图标
        .ivu-icon {
            cursor: pointer;
        }
    }

    .append-option-button {
        width: 90px;
        justify-self: end;
    }

    .ivu-btn-dashed {
        color: #bbb;

        &:hover {
            color: $primaryColor;
        }
    }

    // 总是显示复合题小题的关闭按钮
    .ivu-tabs-tab .ivu-tabs-close {
        width: 22px !important;
        transform: translateZ(0) !important;
        margin-right: -6px !important;
    }
}
</style>
