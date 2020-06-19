<!--
功能: 分数显示组件

属性:
score: 分数 (字符串)

案例:
<Score :score="20"/>
-->
<template>
    <div class="score">
        {{ score }}
    </div>
</template>

<script>
export default {
    props: {
        score: { type: [String, Number], required: true }, // 分数
    },
};
</script>

<style lang="scss">
.score {
    position: relative;
    top   : 0;
    right : 0;
    width : 60px;
    height: 36px;
    font-size: 22px;
    font-family: STXingKai;
    text-align: center;
    color: $errorColor;


    &::after {
        position: absolute;
        content: '';
        width : 100%;
        height: 20px;
        bottom: -4px;
        left  : 0;
        background-image: url(/static-s/img/exam/underscore.png);
        background-size: contain;
        background-repeat: no-repeat;
    }
}
</style>
