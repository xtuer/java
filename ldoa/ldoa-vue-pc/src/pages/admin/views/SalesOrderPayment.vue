<!-- 订单收款情况 -->
<template>
    <div class="sales-order-payment list-page">
        <!-- 顶部工具栏 -->
        <div class="list-page-toolbar-top">
            <RadioGroup v-model="tab" type="button" button-style="solid">
                <Radio label="所有订单"></Radio>
                <Radio label="应收款订单"></Radio>
                <Radio label="本月已收款"></Radio>
                <Radio label="本年已收款"></Radio>
            </RadioGroup>
        </div>


        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat">更多...</Button>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            tab: '所有订单',
            loading: false,
            more: false,
        };
    },
    mounted() {},
    methods: {}
};
</script>

<style lang="scss">
.sales-order-payment {

}
</style>
