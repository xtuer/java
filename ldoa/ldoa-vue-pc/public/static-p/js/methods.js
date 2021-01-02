/**
 * 下载 url 指定的文件
 *
 * @param  {String} url 要下载的 URL
 * @return 无返回值
 */
const download = function(url) {
    // 1. 如果 url 是文件仓库的 url，则替换为下载使用的 url
    // 2. 创建 form 表单
    // 3. 提交表单下载文件

    if (!url) {
        return;
    }

    // [1] 如果 url 是文件仓库的 url，则替换为下载使用的 url
    const finalUrl = url.replace('/file/repo', '/file/download');

    // [2] 创建 form 表单
    const form = document.createElement('form');
    form.method = 'GET';
    form.action = finalUrl;
    document.body.appendChild(form);

    // [3] 提交表单下载文件
    form.submit();
};

/**
 * 返回上一路由
 *
 * 案例: <Button @click="goBack()">返回</Button>
 *
 * @return 无返回值
 */
const goBack = function() {
    window.history.length > 1 ? this.$router.go(-1) : this.$router.push('/');
};

/**
 * 判断是否系统管理员
 *
 * @param {String} role 角色
 * @return 是系统管理员返回 true，否则返回 false
 */
const isSystemAdmin = function(user) {
    return user.roles.includes('ROLE_ADMIN_SYSTEM');
};

// 状态: { "初始化", "审批中", "审批拒绝", "审批通过", "完成" }
/**
 * 返回状态对应的颜色，给 Tag 使用，参考 https://iviewui.com/components/tag
 * <Tag color="red">red</Tag>
 *
 * @param {Int} state 状态值
 * @return {String} Tag 的颜色
 */
const stateColor = function(state) {
    switch (state) {
    case 0: return 'default'; // 初始化
    case 1: return 'cyan';    // 审批中
    case 2: return 'error';   // 审批拒绝
    case 3: return 'primary'; // 审批通过
    case 4: return 'success'; // 完成
    default: return 'default';
    }
};

export default {
    download,
    goBack,
    isSystemAdmin,
    stateColor,
};
