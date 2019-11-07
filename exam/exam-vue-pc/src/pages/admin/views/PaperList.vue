<!--
搜索用户、分页加载 (加载下一页的用户)
-->
<template>
    <div class="papers">
        <!-- 搜索工具栏 -->
        <div class="toolbar">
            <span>试卷标题:</span>
            <Input v-model="filter.title" placeholder="请输入用户名"/>
            <Button :loading="loading" type="primary" @click="searchPapers">搜索</Button>
        </div>

        <!-- 显示试卷 -->
        <Table :columns="columns" :loading="loading" :data="papers">
            <template slot-scope="{ row: paper }" slot="id">
                <Button :to="{ name: 'paper-edit', params: { id: paper.id } }">编辑试卷 {{ paper.id }}</Button>
            </template>
        </Table>

        <Button type="primary" :to="{ name: 'paper-edit', params: { id: '0' } }" style="margin-top: 12px">创建试卷</Button>

        <!-- 加载下一页用户按钮 -->
        <Button :loading="loading" v-show="more" @click="fetchMorePapers">更多...</Button>
    </div>
</template>

<script>
import UserDao from '@/../public/static/js/dao/UserDao';

export default {
    data() {
        return {
            papers: [],
            filter: { // 搜索条件
                title: '',
                pageSize  : 10,
                pageNumber: 1,
            },
            more   : false, // 是否还有更多用户
            loading: false, // 加载中
            columns: [
                { slot: 'id', title: '试卷 ID' },
            ],
        };
    },
    mounted() {
        this.searchPapers();
    },
    methods: {
        // 搜索用户
        searchPapers() {
            this.papers = [];
            this.filter.pageNumber = 1;
            this.more = false;
            this.fetchMorePapers();
        },
        // 点击更多按钮加载下一页的用户
        fetchMorePapers() {
            this.loading = true;

            PaperDao.findPapersOfCurrentOrg(this.filter).then(papers => {
                this.papers.push(...papers);

                this.filter.pageNumber++;
                this.more = papers.length >= this.filter.pageSize;
                this.loading = false;
            });
        },
    }
};
</script>

<style lang="scss">
.papers {
    .toolbar {
        display: grid;
        grid-template-columns: max-content 200px 100px;
        grid-gap: 12px;
        align-items: center;
    }
}
</style>
