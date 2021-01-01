<template>
    <div class="audit-received">
        <AuditItems :auditor-id="userId"/>
    </div>
</template>

<script>
import AuditItems from '@/components/AuditItems.vue';

export default {
    components: { AuditItems },
    computed: {
        userId() {
            return this.$store.state.user.userId;
        }
    }
};
</script>

<style lang="scss">

</style>
