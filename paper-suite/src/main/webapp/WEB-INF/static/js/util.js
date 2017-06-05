/**
 * 扩展了 String 类型，给其添加格式化的功能，替换字符串中 {placeholder} 或者 {0}, {1} 等模式部分为参数中传入的字符串
 * 使用方法:
 *     'I can speak {language} since I was {age}'.format({language: 'Javascript', age: 10})
 *     'I can speak {0} since I was {1}'.format('Javascript', 10)
 * 输出都为:
 *     I can speak Javascript since I was 10
 *
 * @param replacements 用来替换 placeholder 的 JSON 对象或者数组
 */
String.prototype.format = function(replacements) {
    replacements = (typeof replacements === 'object') ? replacements : Array.prototype.slice.call(arguments, 0);
    return formatString(this, replacements);
};

/**
 * 替换字符串中 {placeholder} 或者 {0}, {1} 等模式部分为参数中传入的字符串
 * 使用方法:
 *     formatString('I can speak {language} since I was {age}', {language: 'Javascript', age: 10})
 *     formatString('I can speak {0} since I was {1}', 'Javascript', 10)
 * 输出都为:
 *     I can speak Javascript since I was 10
 *
 * @param str 带有 placeholder 的字符串
 * @param replacements 用来替换 placeholder 的 JSON 对象或者数组
 */
var formatString = function (str, replacements) {
    replacements = (typeof replacements === 'object') ? replacements : Array.prototype.slice.call(arguments, 1);
    return str.replace(/\{\{|\}\}|\{(\w+)\}/g, function(m, n) {
        if (m == '{{') { return '{'; }
        if (m == '}}') { return '}'; }
        return replacements[n];
    });
};

/*-----------------------------------------------------------------------------|
 |                                    Array                                    |
 |----------------------------------------------------------------------------*/

/**
 * 从数组中删除下标为 index 的元素
 *
 * @param  {Integer} index 要删除的元素的下标
 * @return 无返回值
 */
Array.prototype.remove = function(index) {
    this.splice(index, 1);
};

/**
 * 在下标为 index 处向数组中插入一个元素，例如 arr.remove(2)
 *
 * @param  {Integer}     index 要插入元素的位置
 * @param  {ElementType} elem  要插入的元素
 * @return 无返回值
 */
Array.prototype.insert = function(index, elem) {
    this.splice(index, 0, elem);
};

/**
 * 替换数组中下标为 index 处的元素
 *
 * @param  {Integer}     index 要替换的元素的位置
 * @param  {ElementType} elem  用于替换的元素
 * @return 无返回值
 */
Array.prototype.replace = function(index, elem) {
    if (index>=0 && index<this.length) {
        this.splice(index, 1, elem);
    }
};

/**
 * 清空数组
 *
 * @return 无返回值
 */
Array.prototype.empty = function() {
    this.splice(0, this.length);
};

/*-----------------------------------------------------------------------------|
 |                                    Util                                     |
 |----------------------------------------------------------------------------*/
function Util() {

}

/**
 * 设置 sidebar 的第 activateSidebarItem 个 item 为当前也的 item，例如 arr.insert(3, 'Alice')
 *
 * @param  {[type]} sidebarItemIndex item 的下标，从 0 开始
 * @return 无返回值
 */
Util.activateSidebarItem = function(sidebarItemIndex) {
    $('.sidebar .item').eq(sidebarItemIndex).addClass('active').click(function(event) {
        event.preventDefault();
    });
};

/*-----------------------------------------------------------------------------|
 |                                  TreeUtils                                  |
 |----------------------------------------------------------------------------*/
function TreeUtils() {

}

/**
 * 取得所有的跟节点，ztree 树可以有多个跟节点，其实是一个森林
 *
 * @param  {object} ztree ztree 对象
 * @return 返回树的所有跟节点
 */
TreeUtils.getRoots = function(ztree) {
    return ztree.getNodesByFilter(function(treeNode) {
        return (treeNode.level === 0);
    }, false);
};

/**
 * 取得树的所有节点
 *
 * @param  {object} ztree ztree 对象
 * @return 返回树的所有节点
 */
TreeUtils.getAllNodes = function(ztree) {
    return ztree.getNodesByFilter(function(treeNode) {
        return true;
    }, false);
};
