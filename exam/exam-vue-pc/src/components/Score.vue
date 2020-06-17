<!--
功能: 分数显示组件

属性:
score: 分数 (数字、字符串)

案例:
<Score :score="20"/>
-->
<template>
    <div class="score">
        <div>
            <InputNumber v-if="editable" v-model="scoreX" :min="0" :max="max" :step="0.5" size="small" @on-change="$emit('on-change', scoreX || 0)"/>
            <span v-else>{{ score }}</span>
        </div>
        <img src="/static/img/underline.png">
    </div>
</template>

<script>
export default {
    props: {
        score   : { type: [String, Number], required: true }, // 分数
        max     : { type: Number, default: 0 }, // 最高分数
        editable: { type: Boolean, default: false }, // 分数是否可编辑
    },
    model: {
        prop : 'score',
        event: 'on-change',
    },
    data() {
        return {
            scoreX: this.score || 0,
        };
    },
    watch: {
        score() {
            this.scoreX = this.score;
        }
    }
};
</script>

<style lang="scss">
.score {
    display: inline-flex;
    align-items: center;
    font-size: 26px;
    font-family: STXingKai;
    text-align: center;
    color: $errorColor;
    background: white;

    img {
        width : 50px;
        height: 50px;
        object-fit: contain;
        margin-left: 2px;
    }
}
</style>
