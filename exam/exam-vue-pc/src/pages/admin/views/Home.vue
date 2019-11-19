<template>
    <div class="home">
        <Button :to="{ name: 'papers' }">试卷列表</Button>
        <Button :to="{ name: 'exams' }">考试列表</Button>
    </div>
</template>

<style lang="scss">
.home {
    a {
        margin-right: 12px;
    }
}
</style>
