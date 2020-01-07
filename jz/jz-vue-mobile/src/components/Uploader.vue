<!--
上传组件
-->
<template>
    <div class="uploader">
        <div class="label">{{ label }}</div>
            <van-image :src="src" fit="cover">
                <template v-slot:loading>
                    <van-loading type="spinner" size="20" />
                </template>
            </van-image>
            <van-uploader :max-size="5000000" :after-read="upload">
                <van-button :loading="loading" type="primary" icon="add-o"></van-button>
            </van-uploader>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        label: { type: String, default: '' },
        src  : { type: String },
    },
    model: {
        prop : 'src',
        event: 'on-success' // 内容发生变化后触发, 参数为编辑的内容
    },
    data() {
        return {
            loading: false,
        };
    },
    methods: {
        // 返回布尔值
        // beforeRead(file) {
        //     // console.log(file.type);
        //     this.$toast(file.type);

        //     if (file.type === 'image/jpeg' || file.type === 'image/jpg') {
        //         // this.$toast('请上传 jpg 格式图片');
        //         return true;
        //     }

        //     return true;
        // },
        // 返回 Promise
        asyncBeforeRead(file) {
            this.$toast(file.type);

            return new Promise((resolve, reject) => {
                // if (file.type !== 'image/jpeg') {
                //     // this.$toast('请上传 jpg 格式图片');
                //     resolve();
                // } else {
                //     resolve();
                // }
                resolve();
            });
        },
        upload(file) {
            let url = '/form/upload/temp/file';
            let fd  = new FormData();
            fd.append('file', file.file);

            this.loading = true;
            Rest.upload(url, fd).then(result => {
                this.loading = false;

                if (result.success) {
                    this.$emit('on-success', result.data.url);
                } else {
                    this.$toast.fail(`上传失败: ${result.message}`);
                }
            }).catch(e => {
                this.loading = false;
                this.$toast.fail('上传失败');
            });
        }
    }
};
</script>

<style lang="scss">
.uploader {
    display: grid;
    grid-template-columns: max-content 1fr max-content;
    align-items: center;
    grid-gap: 6px;
    padding: 6px;

    .label {
        text-align: right;
    }

    .van-image {
        // width: 100px;
        height: 44px;
        border-radius: 2px;
        overflow: hidden;

        img {
            border-radius: 2px;
        }
    }
}
</style>
