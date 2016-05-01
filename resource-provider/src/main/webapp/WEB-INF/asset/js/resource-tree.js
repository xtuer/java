/**
 * 创建显示资源的树
 *
 * @param elem 要在这个元素上显示树, zTree 使用
 * @param resourceUrl 第三方资源的接口
 */
function ResourceTree(elem, resourceUrl) {
    this.elem = elem;
    this.resourceUrl = resourceUrl;
    this.zTreeObj = null;
    this.requestResourceUrl = '/request-resources'; // 向后台请求加载资源的 URL

    this.setting = {
        callback: {
            beforeCheck: function(treeId, treeNode, clickFlag) {
                return !treeNode.isParent; // 当是父节点时，返回 false，不让选取
            }
        },
        check: {
            enable: true
        },
        view: {
            showIcon: false
        }
    };

    this.load();
}

/**
 * 向我们的服务器请求加载资源描述信息
 */
ResourceTree.prototype.load = function() {
    var data = {resourceUrl: this.resourceUrl};
    var self = this;

    // 为了不使用 JSONP，可以把 URL 传给后台，后台获取 xml 数据后 AJAX 返回来
    $.ajax({
        url: this.requestResourceUrl,
        type: 'POST', // 1. 不能是 GET
        dataType: 'json',
        contentType: 'application/json', // 2. 少了就会报错
        data: JSON.stringify(data) // 3. data 需要序列化一下
    })
    .done(function(data) {
        self.createTree(data.result);
    })
    .fail(function(error) {
        console.log(error.responseText);
    });
}

/**
 * 使用资源的 XML 描述信息创建可视化的树，方便用户进行资源选择
 *
 * @param xml 资源的 XML 描叙
 */
ResourceTree.prototype.createTree = function(xml) {
    var treeNodes = [
        {name: '共享资源', open: true, children: []} // 增加根节点辅助编程
    ];

    var self = this;

    $(xml).find('textbook_list > textbook').each(function() {
        // 转换路径为树的节点: 高中语文/人教课标版/高一/必修1梳理探究/优美的汉字
        var path = $(this).text();
        var names = path.split('/');
        var node = treeNodes[0]; // 路径从根节点开始遍历

        for (var i = 0; i < names.length; ++i) {
            var name = names[i];
            var n = self.findChild(node, name);

            if (n != null) {
                node = n;
            } else {
                n = self.createNode(name);

                // 最后一个是叶子节点，则显示 checkbox 并保存资源的路径到此节点的数据域中
                if (i == names.length - 1) {
                    n.path = path;
                    n.nocheck = false;
                }

                node.children.push(n);
                node = n;
            }
        }
    });

    treeNodes = treeNodes[0].children; // 去掉不必要的根节点
    self.zTreeObj = $.fn.zTree.init(this.elem, this.setting, treeNodes);
}

// 创建 zTree 使用的节点
ResourceTree.prototype.createNode = function(name) {
    return {
        name: name,
        open: true,
        children: [],
        nocheck: true
    }
}

// 如果在 node 的子节点里找不到，则不是他的子节点，返回 null
ResourceTree.prototype.findChild = function(node, name) {
    for (var i = 0; i < node.children.length; ++i) {
        if (name == node.children[i].name) {
            return node.children[i];
        }
    }

    return null;
}

// 取得所有选中的点对应的资源路径
ResourceTree.prototype.getSelectedResourcePaths = function() {
    var nodes = this.zTreeObj.getCheckedNodes(true);
    var paths = [];

    for (var i = 0; i < nodes.length; ++i) {
        if (!nodes[i].isParent) {
            paths.push(nodes[i].path);
        }
    }

    return paths;
}
