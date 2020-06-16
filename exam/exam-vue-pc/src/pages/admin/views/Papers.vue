<!-- 试卷列表 -->
<template>
    <div class="about">
        <!-- 顶部工具栏 -->
        <div class="toolbar-1">
            <Input search enter-button placeholder="请输入试卷名" @on-search="searchPapers"/>
            <Button type="primary" icon="md-add">添加试卷</Button>
        </div>

        <!-- 试卷列表 -->
        <Table :data="papers" :columns="columns" :loading="reloading" border>
            <!-- 介绍信息 -->
            <template slot-scope="{ row: paper }" slot="info">
                ---
            </template>

            <!-- 操作按钮 -->
            <template slot-scope="{ row: paper }" slot="action">
                <Button type="primary" size="small" style="margin-right: 5px">编辑</Button>
                <Button type="error" size="small">删除</Button>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="toolbar-2">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMorePapers">更多...</Button>
        </div>
    </div>
</template>

<script>
import ExamDao from '@/../public/static-p/js/dao/ExamDao';

export default {
    data() {
        return {
            papers : [],
            filter: { // 搜索条件
                title     : '',
                pageSize  : 2,
                pageNumber: 1,
            },
            more     : false, // 是否还有更多试卷
            loading  : false, // 加载中
            reloading: false,
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { key : 'title',  title: '名字', width: 150 },
                { slot: 'info',   title: '介绍', minWidth: 500 },
                { slot: 'action', title: '操作', width: 150, align: 'center' },
            ]
        };
    },
    mounted() {
        this.searchPapers();
    },
    methods: {
        // 搜索试卷
        searchPapers() {
            this.papers            = [];
            this.more              = false;
            this.reloading         = true;
            this.filter.pageNumber = 1;

            this.fetchMorePapers();
        },
        // 点击更多按钮加载下一页的试卷
        fetchMorePapers() {
            this.loading = true;

            ExamDao.findPapersOfCurrentOrg(this.filter).then(papers => {
                this.papers.push(...papers);

                this.more      = papers.length >= this.filter.pageSize;
                this.loading   = false;
                this.reloading = false;
                this.filter.pageNumber++;
            });
        },
    }
};
</script>

<style lang="scss">
.about {
    display: grid;
    grid-gap: 24px;

    .toolbar-1 {
        display: grid;
        grid-template-columns: 400px max-content;
        justify-content: space-between;
        align-items: center;
    }
    .toolbar-2 {
        display: grid;
        justify-content: center;
        align-items: center;
    }
}
</style>
