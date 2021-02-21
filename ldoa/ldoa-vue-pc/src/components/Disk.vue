<!--
描述: 网盘的组件

属性:
user-id: 用户 ID

事件: 无
Slot: 无

示例:
<Disk user-id="12345"/>

-->
<!-- eslint-disable vue/no-parsing-error -->

<!--
搜索文件、分页加载 (加载下一页的文件)
-->
<template>
    <div class="files list-page">
        <!-- 顶部工具栏 -->
        <div class="list-page-toolbar-top">
            <!-- 搜索条件 -->
            <div class="filter">
                <!-- 指定条件的搜索 -->
                <Input v-model="filter.filename" placeholder="请输入文件名" search enter-button @on-search="searchFiles">
                    <span slot="prepend">文件名称</span>
                </Input>
            </div>

            <!-- 其他按钮 -->
            <FileUpload v-if="my" @on-success="fileUploaded"/>
        </div>

        <!-- 文件列表 -->
        <Table :data="files" :columns="columns" :loading="reloading" border>
            <!-- 介绍信息 -->
            <template slot-scope="{ row: file }" slot="createdAt">
                {{ file.createdAt | formatDate('YYYY-MM-DD HH:mm') }}
            </template>

            <!-- 操作按钮 -->
            <template slot-scope="{ row: file }" slot="action">
                <Button type="primary" size="small" @click="download(file.url)">下载</Button>
                <Button v-if="my" type="error" size="small" @click="deleteFile(file)">删除</Button>
            </template>
        </Table>

        <!-- 底部工具栏 -->
        <div class="list-page-toolbar-bottom">
            <Button v-show="more" :loading="loading" shape="circle" icon="md-boat" @click="fetchMoreFiles">更多...</Button>
        </div>
    </div>
</template>

<script>
import DiskDao from '@/../public/static-p/js/dao/DiskDao';
import FileUpload from '@/components/FileUpload.vue';

export default {
    props: {
        userId: { type: String, required: false, default: '0' }, // 用户 ID
    },
    components: { FileUpload },
    data() {
        return {
            files : [],
            filter: { // 搜索条件
                userId    : this.userId,
                filename  : '', // 文件名
                pageSize  : 50,
                pageNumber: 1,
            },
            more     : false, // 是否还有更多文件
            loading  : false, // 加载中
            reloading: false,
            columns  : [
                // 设置 width, minWidth，当大小不够时 Table 会出现水平滚动条
                { key : 'filename',  title: '文件名称', minWidth: 150 },
                { key : 'nickname',  title: '上传用户', width: 150 },
                { slot: 'createdAt', title: '上传时间', width: 150, align: 'center' },
                { slot: 'action',    title: '操作', width: 150, align: 'center', className: 'table-action' },
            ]
        };
    },
    mounted() {
        this.searchFiles();
    },
    computed: {
        // 当 userId 不为 0 时，表示显示的是我的文件，可以执行上传、删除操作
        my() {
            return this.userId !== '0';
        }
    },
    methods: {
        // 搜索文件
        searchFiles() {
            this.files             = [];
            this.more              = false;
            this.reloading         = true;
            this.filter.pageNumber = 1;

            this.fetchMoreFiles();
        },
        // 点击更多按钮加载下一页的文件
        fetchMoreFiles() {
            this.loading = true;

            DiskDao.findDiskFiles(this.filter).then(files => {
                this.files.push(...files);

                this.more      = files.length >= this.filter.pageSize;
                this.loading   = false;
                this.reloading = false;
                this.filter.pageNumber++;
            });
        },
        // 文件上传成功
        fileUploaded(file) {
            // 文件上传成功后保存到网盘
            DiskDao.saveFileToDisk(file.id).then(diskFile => {
                this.files.unshift(diskFile); // 添加到最前面
                this.$Message.success('上传成功');
            });
        },
        // 删除文件
        deleteFile(file) {
            // 1. 删除提示
            // 2. 从服务器删除成功后才从本地删除
            // 3. 提示删除成功

            this.$Modal.confirm({
                title: `确定删除 <font color="red">${file.filename}</font> 吗?`,
                loading: true,
                onOk: () => {
                    DiskDao.deleteDiskFile(file.fileId).then(() => {
                        const index = this.files.findIndex(f => f.fileId === file.fileId); // 文件下标
                        this.files.splice(index, 1); // [2] 从服务器删除成功后才从本地删除
                        this.$Modal.remove();
                        this.$Message.success('删除成功');
                    });
                }
            });
        }
    }
};
</script>

<style lang="scss">
</style>
