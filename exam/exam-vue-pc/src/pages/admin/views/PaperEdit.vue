<!-- 试卷编辑 -->
<template>
    <div class="paper-edit">
        Paper Edit
    </div>
</template>

<script>
export default {
    data() {
        return {
            paperId: this.$route.params.paperId, // 试卷 ID
        };
    },
    mounted() {},
    methods: {}
};
</script>

<style lang="scss">
.paper-edit {

}
</style>
