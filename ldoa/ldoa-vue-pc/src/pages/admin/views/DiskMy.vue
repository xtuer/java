<!-- 网盘中我的文件 -->
<template>
    <div class="disk-my">
        <Disk :user-id="userId"/>
    </div>
</template>

<script>
import Disk from '@/components/Disk.vue';

export default {
    components: { Disk },
    computed: {
        // 登陆用户的 ID
        userId() {
            return this.$store.state.user.userId;
        }
    }
};
</script>
