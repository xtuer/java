<!-- 普通试卷编辑 -->
<template>
    <!-- 显示和编辑分数 -->
    <PaperEdit :paper-id="paperId" :paper-type="0" score-show score-edit/>
</template>

<script>
import PaperEdit from '@/components/PaperEdit.vue';

export default {
    components: { PaperEdit },
    data() {
        return {
            paperId: this.$route.params.id + '',
        };
    },
};
</script>
