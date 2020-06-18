<!-- 试卷列表 -->
<template>
    <div class="papers layout-list">
        <!-- 顶部工具栏 -->
        <div class="layout-list-toolbar-top">
            <Input v-model="filter.title" search enter-button placeholder="请输入试卷标题" @on-search="searchPapers"/>
            <Button type="primary" icon="md-add" @click="createPaper()">添加试卷</Button>
        </div>

        <!-- 试卷列表 -->
        <Table :data="papers" :columns="columns" :loading="reloading" border>
            <!-- 时间 -->
            <template slot-scope="{ row: paper }" slot="createdAt">
                {{ paper.createdAt | formatDate('YYYY-MM-DD') }}
            </template>
            <template slot-scope="{ row: paper }" slot="updatedAt">
                {{ paper.updatedAt | formatDate('YYYY-MM-DD') }}
            </template>

            <!-- 操作按钮 -->
            <template slot-scope="{ row: paper }" slot="action">
                <div class="column-buttons">
                    <Button type="primary" size="small" @click="toPaperEdit(paper.id)">编辑</Button>
                    <Button type="error" size="small" @click="deletePaper(paper)">删除</Button>
                </div>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="layout-list-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMorePapers">更多...</Button>
        </div>
    </div>
</template>

<script>
import PaperDao from '@/../public/static-p/js/dao/PaperDao';

export default {
    data() {
        return {
            papers: [],
            filter: { // 搜索条件
                title     : '',
                pageSize  : 20,
                pageNumber: 1,
            },
            more     : false, // 是否还有更多试卷
            loading  : false, // 加载中
            reloading: false,
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { key : 'title',         title: '试卷标题', minWidth: 150 },
                { key : 'totalScore',    title: '满分', width: 150, align: 'center' },
                { key : 'questionCount', title: '题目数量', width: 150, align: 'center' },
                { slot: 'createdAt',     title: '创建时间', width: 150, align: 'center' },
                { slot: 'updatedAt',     title: '修改时间', width: 150, align: 'center' },
                { slot: 'action',        title: '操作', width: 150, align: 'center' },
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

            PaperDao.findPapersOfCurrentOrg(this.filter).then(papers => {
                this.papers.push(...papers);

                this.more      = papers.length >= this.filter.pageSize;
                this.loading   = false;
                this.reloading = false;
                this.filter.pageNumber++;
            });
        },
        // 创建试卷
        createPaper() {
            const paper = { id: '0', title: '新创建的试卷' };

            PaperDao.upsertPaper(paper).then(newPaper => {
                this.toPaperEdit(newPaper.id);
            });
        },
        // 删除试卷
        deletePaper(paper) {
            // 1. 删除提示
            // 2. 从服务器删除成功后才从本地删除
            // 3. 提示删除成功

            this.$Modal.confirm({
                title: `确定删除 <font color="red">${paper.title}</font> 吗?`,
                loading: true,
                onOk: () => {
                    PaperDao.deletePaper(paper.id).then(() => {
                        const index = this.papers.findIndex(p => p.id === paper.id); // 试卷下标
                        this.papers.splice(index, 1); // [2] 从服务器删除成功后才从本地删除
                        this.$Modal.remove();
                        this.$Message.success('删除成功');
                    });
                }
            });
        },
        // 跳转到试卷编辑页面
        toPaperEdit(paperId) {
            this.$router.push({ name: 'paper-edit', params: { paperId } });
        }
    }
};
</script>

<style lang="scss">
</style>
