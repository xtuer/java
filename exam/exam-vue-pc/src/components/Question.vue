<!--
功能: 题目组件

参数:
question: [必选] 题目
editable: [可选] 是否可编辑
border  : [可选] 是否显示边框 (readonly 模式下用)
toolbar : [可选] 是否显示工具栏 (readonly 模式下用)
paper-edit: [可选] 是否编辑试卷模式 (readonly 模式下用)
score-show: [可选] 是否显示分数 (readonly 模式下用)

事件:
on-append-question-to-group-click(groupQuestion): 点击题型题的添加题目按钮触发，参数为题型题
on-edit-question-click(question)  : 点击编辑题目按钮触发，参数为编辑的题目
on-delete-question-click(question): 点击删除题目按钮触发，参数为删除的题目
on-append-sub-question(): 添加小题时触发，参数无
on-delete-sub-question(): 删除小题时触发，参数无
on-score-change(question): 题目的满分变化时触发，参数为题目

Slot: 无

示例:
<Question :question="question" editable/>

只读题目的布局:
question
    stem
    option
        mark, description
    tfngs
        tfng
    key
    answer
-->
<template>
    <div :class="questionClass">
        <!-- 编辑题目 -->
        <template v-if="editable">
            <!-- 复合题 -->
            <template v-if="question.type===6">
                <!-- 1. 复合题: 题干，递归显示小题 -->
                <Tabs v-model="activeTabName" :animated="false" type="card" @on-tab-remove="deleteSubQuestion">
                    <!-- 复合题大题: 只有题干 -->
                    <TabPane label="复合题" name="big">
                        <RichText v-model="question.stem" :min-height="150" inline/>
                    </TabPane>

                    <!-- 复合题小题: 递归显示小题 -->
                    <TabPane v-for="subQuestion in subQuestions" :key="subQuestion.id" :label="questionTypeName(subQuestion.type)" :name="subQuestion.id" closable>
                        <Question :question="subQuestion" editable/>
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
            <template v-else-if="question.type===6">
                <RichText v-model="question.stem" :min-height="150" inline/>
            </template>

            <!-- 其他题: 选择题、判断题、填空题、问答题 -->
            <template v-else>
                <!--
                    3. 选择题: 题干、选项、解析
                    4. 判断题: 题干、选项、解析
                    5. 填空题、问答题: 题干、答案、解析
                -->

                <!-- [2] 题型题: 题干 ([5] 填空题、问答题) -->
                <RichText v-model="question.stem" :min-height="150" inline/>

                <!-- [3] 选择题: 题干、选项、答案、解析 -->
                <template v-if="question.type===1 || question.type===2">
                    <div v-for="option in options" :key="option.id" class="option">
                        <div class="mark" :class="{ correct: option.correct }" @click="markCorrectOption(option)">{{ option.mark }}</div>
                        <RichText v-model="option.description" inline class="description"/>
                        <Icon type="md-close" size="18" class="close" @click="deleteOption(option)"/>
                    </div>
                    <Button type="dashed" size="small" icon="md-add" class="append-option-button" @click="appendOption">添加选项</Button>
                </template>

                <!-- [4] 判断题: 选项 (正确、错误) -->
                <template v-if="question.type===3">
                    <div class="tfngs">
                        <div v-for="option in options" :key="option.id" :class="{ tfng: true, correct: option.correct }" @click="markCorrectOption(option)">
                            {{ option.description }}
                        </div>
                    </div>
                </template>

                <!-- 答案: 填空题和问答题才需要答案 -->
                <div v-if="question.type===4 || question.type===5" class="key">
                    <div>答案:</div>
                    <RichText v-model="question.key" :min-height="150" inline/>
                </div>

                <!-- 解析 (题型题不需要) -->
                <div v-if="question.type!==7" class="analysis">
                    <div>解析:</div>
                    <RichText v-model="question.analysis" :min-height="150" inline/>
                </div>
            </template>
        </template>

        <!-- 只读题目 -->
        <template v-else>
            <!--
                A. 题型题: 题干
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

            <!-- [1] 题干: 编辑试卷模式 -->
            <div class="stem">
                <div>{{ question.snLabel }}</div>
                <div class="stem-content">
                    <div v-html="question.stem"></div>

                    <!--
                        1. 给定分数 (编辑试卷时)
                            每题得分都一样: 在题型题上给分 (每题得分)，包括单选题、多选题、判断题、填空题
                            每题得分不一样: 复合题的小题、问答题
                        2. 显示分数 (预览试卷时)
                    -->

                    <!-- [1] 给定分数 (编辑试卷时) -->
                    <template v-if="paperEdit">
                        <!-- 每题得分都一样 -->
                        <div v-if="scoreGroup" class="group-score-edit">
                            ，每题 <InputNumber v-model="question.score" :min="1" :step="0.5" size="small" @on-change="$emit('on-score-change', question)"/> 分，共 {{ question.totalScore }} 分
                        </div>
                        <!-- 每题得分不一样 -->
                        <div v-else-if="scoreSelf">
                            <InputNumber v-model="question.totalScore" :min="1" :step="0.5" size="small" @on-change="$emit('on-score-change', question)"/> 分
                        </div>
                        <div v-else-if="question.type===7">
                            ，共 {{ question.totalScore }} 分
                        </div>
                    </template>

                    <!-- [2] 显示分数 (预览试卷时) -->
                    <template v-else-if="!paperEdit && scoreShow">
                        <div v-if="scoreGroup">，每题 {{ question.score }} 分，共 {{ question.totalScore }} 分
                        </div>
                        <div v-else-if="scoreSelf" style="margin-left: 6px">
                            ({{ question.score }} 分)
                        </div>
                        <div v-else-if="question.type===7">
                            ，共 {{ question.totalScore }} 分
                        </div>
                    </template>
                </div>

                <!-- 工具栏 -->
                <div v-if="toolbar" class="toolbar">
                    <Icon v-if="question.type===7" type="md-add-circle" @click="$emit('on-append-question-to-group-click', question)"/>
                    <Icon v-if="question.type!==7" type="ios-create" @click="$emit('on-edit-question-click', question)"/>
                    <Icon type="md-trash" @click="$emit('on-delete-question-click', question)"/>
                </div>
            </div>

            <!-- [2] 选择题: 选项 -->
            <template v-if="question.type===1 || question.type===2">
                <div v-for="option in options" :key="option.id" class="option">
                    <div class="mark" :class="{ correct: option.correct }">{{ option.mark }}</div>
                    <div class="description" v-html="option.description"></div>
                </div>
            </template>

            <!-- [3] 判断题: 选项 (正确、错误) -->
            <template v-if="question.type===3">
                <div class="tfngs">
                    <div v-for="option in options" :key="option.id" :class="{ tfng: true, correct: option.correct }"> {{ option.description }}</div>
                </div>
            </template>

            <!-- [5] 复合题: 递归显示小题 -->
            <template v-if="question.type===6">
                <Question v-for="subQuestion in subQuestions" :key="subQuestion.id"
                        :question="subQuestion" :paper-edit="paperEdit" :score-show="scoreShow"
                        @on-score-change="$emit('on-score-change', subQuestion)"/>
            </template>
        </template>
    </div>
</template>

<script>
export default {
    name: 'Question', // 递归组件需要设置 name 属性，才能在模板中调用自己
    props: {
        question: { type: Object, required: true  }, // 题目
        editable: { type: Boolean, default: false }, // 是否可编辑
        border  : { type: Boolean, default: false }, // 是否显示边框
        toolbar : { type: Boolean, default: false }, // 是否显示工具栏
        paperEdit: { type: Boolean, default: false }, // 编辑试卷模式
        scoreShow: { type: Boolean, default: false }, // 编辑试卷模式
    },
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
        // 恢复当前 Tab 为题干的 Tab
        resetActiveTabName() {
            this.activeTabName = 'big';
        }
    },
    computed: {
        // 题目的样式
        questionClass() {
            return {
                question           : true,
                'question-group'   : this.question.type === QUESTION_TYPE.DESCRIPTION, // 题型题，题型分组
                'question-complex' : this.question.type === QUESTION_TYPE.COMPLEX, // 复合题
                'question-sub'     : Utils.isValidId(this.question.parentId), // 复合题的小题
                'question-editable': this.editable,
                'question-readonly': !this.editable,
                'question-paper-edit': this.paperEdit,
                border: this.border,
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
            return QUESTION_TYPES.filter(q => q.value !== QUESTION_TYPE.COMPLEX);
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
        }
    }
};
</script>

<style lang="scss">
.question {
    display: grid;
    grid-gap: 12px;
    cursor: default;

    &.border {
        border: 1px solid #ddd;
        border-radius: 6px;
        padding: 12px;
    }

    // 选项
    .option {
        // 标记: A, B, C, D
        .mark {
            @include alignCenter;
            border: 1px solid rgb(211, 211, 211);
            border-radius: 100%;
            transition: all .6s;

            &.correct {
                color: white;
                background: $lightPrimaryColor;
            }
        }
    }

    // 判断题
    .tfngs {
        display: grid;
        grid-template-columns: 1fr 1fr;
        width: 130px;
        border-radius: 4px;
        border: 1px solid #eee;
        overflow: hidden;

        .tfng {
            @include alignCenter;
            font-size: 12px;
            height: 24px;

            &.correct {
                color: white;
                background: $lightPrimaryColor;
            }
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

        .rich-text .mce-content-body {
            border-radius: 0 4px 4px 0;
        }

        // 删除选项按钮
        .close {
            position: absolute;
            top: 8px;
            right: 8px;
            color: #bbb;
            cursor: pointer;

            &:hover {
                color: $primaryColor;
                transition: all .8s;
            }
        }
    }

    .append-option-button {
        width: 90px;
        justify-self: end;
    }

    .tfngs {
        .tfng {
            cursor: pointer;
            transition: all .6s;
        }
    }

    .ivu-btn-dashed {
        color: #bbb;

        &:hover {
            color: $primaryColor;
        }
    }
}

// 只读题目
.question-readonly {
    // 题干
    .stem {
        display: grid;
        grid-template-columns: max-content 1fr;
        align-items: start;
        position: relative;

        .stem-content {
            > div, > div > p {
                display: inline-block;
            }
        }

        // 题型题的分值样式
        .group-score-edit {
            display: flex;
            align-items: flex-start;
        }

        .ivu-input-number {
            width: 55px;
            margin: auto 6px;
        }

        // 编辑题目的工具栏
        .toolbar {
            position: absolute;
            right: 0px;
            background: white;
            transition: all .6s;
            opacity: 0;
            border-radius: 2px;
            box-shadow: 0 0 2px #aaa;

            .ivu-icon {
                font-size: 18px;
                cursor: pointer;
                margin-left: 6px;

                &:first-child {
                    margin-left: 3px;
                }

                &:hover {
                    color: $primaryColor;
                    transition: all .6s;
                }
            }
        }
    }

    &:hover .stem .toolbar {
        opacity: 1;
    }

    // 选择题选项
    .option {
        display: grid;
        grid-template-columns: max-content 1fr;
        grid-gap: 8px;

        // 选项的标记: A, B, C, D
        .mark {
            width: 24px;
            height: 24px;
        }
    }

    // 编辑试卷时，鼠标移动到题目上，高亮它
    &.question-paper-edit:hover {
        border-radius: 3px;
        box-shadow: 0 0 1px $primaryColor;
        transition: all .5s;
        background: rgba(0, 140, 240, 0.1);
    }

    &.question-group .stem {
        font-size: 18px;
        font-weight: bold;
    }
}
</style>
