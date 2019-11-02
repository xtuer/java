process.env.VUE_APP_VERSION = new Date().getTime();
const CompressionWebpackPlugin = require('compression-webpack-plugin');

module.exports = {
    devServer: {
        port : 8888,
        proxy: 'http://localhost:8080',
        disableHostCheck: true,
    },
    pages: {
        page1: 'src/pages/page1/main.js',
        admin: 'src/pages/admin/main.js',
    },
    css: {
        loaderOptions: {
            sass: {
                prependData: `
                    @import '@/../public/static/css/variables.scss';
                `
            },
        }
    },

    // yarn build 的输出目录
    outputDir: '../exam-web-app/src/main/webapp/WEB-INF/page-pc',
    assetsDir: 'static',
    productionSourceMap: false, // 不生成 map 文件
    configureWebpack: (config) => {
        if (process.env.NODE_ENV === 'production') {
            return {
                plugins: [new CompressionWebpackPlugin({
                    test: /\.(js|css)(\?.*)?$/i, // 需要压缩的文件正则
                    threshold: 10240,            // 文件大小大于这个值时启用压缩 (10K)
                    deleteOriginalAssets: false, // 压缩后保留原文件
                })]
            };
        }
        return null;
    },
};
