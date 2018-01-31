/**
 * 解析资源文件创建 Resource 对象，其 nodes 属性为 zTree 需要的节点数组
 * 
 * @param {jQuery Object} xml XML 内容生成的 jQuery 对象
 * @constructor
 */
function Resource(xml) {
    this.xml = xml;
    this.nodes = []; // 节点的结构为: {name: 'Node_Name', children: [], url: 'http://www.ebag.com/aloha.pdf'}, url 为可选
    this.init();
}

/**
 * 使用 XML 内存初始化资源对象，主要是为了创建 zTree 需要的节点数组
 *
 * @return 无返回值
 */
Resource.prototype.init = function() {
    var self = this;
    $('catalog', self.xml).each(function() {
        var catalogName = $(this).attr('name'); // 目录的名字
        var pathNodes = []; // 此目录下的书
        self.nodes.push({name: catalogName, children: pathNodes, nocheck: true});

        // 此目录下的每一本书
        $('textbook', this).each(function() {
            var bookUrl  = $(this).attr('url');
            var bookPath = $(this).text();
            var names   = bookPath.split('/');
            var startIndex = 0;

            // 第一次时初始化 pathNodes
            if (pathNodes.length === 0) {
                pathNodes.push(Resource.createNode(names[0]));
                startIndex = 1;
            }

            var node = pathNodes[0];
            for (var i = startIndex; i < names.length; ++i) {
                var name = names[i];
                var child = Resource.findChild(node, name);

                if (child !== null) {
                    node = child;
                } else {
                    // 如果没有找到，则创建新的节点
                    child = Resource.createNode(name);

                    // 最后一个是叶子节点，则显示 checkbox 并保存资源的路径到此节点的数据域中
                    if (i == names.length - 1) {
                        child.url = bookUrl;
                        child.nocheck = false;
                    }

                    node.children.push(child);
                    node = child;
                }
            }
        });
    });
};

/**
 * 创建节点
 *
 * @param  {String} name 节点的名字
 * @return {JSON}   返回表示节点的 JSON 对象
 */
Resource.createNode = function(name) {
    return {
        name: name,
        open: true,
        children: [],
        nocheck: true
    };
};

/**
 * 查找 node 下名字为传入的 name 的子节点
 *
 * @param  {String} node 节点
 * @param  {String} name 子节点的名字
 * @return {JSON}   返回找到的子节点，如果找不到则返回 null
 */
Resource.findChild = function(node, name) {
    for (var i = 0; i < node.children.length; ++i) {
        if (name == node.children[i].name) {
            return node.children[i];
        }
    }

    return null;
};
