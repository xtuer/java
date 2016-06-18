/**
FileTree 的 Dom 结构, 说明请参考 http://xtuer.github.io/fe-tree，使用方法如下:
1. 在 HTML 中加入下面代码
    <!-- 目录中的文件或者子目录放在 <ul> 里, 文件或目录是一个 <li>，文件或目录的名字是 <li> 中的 <div> -->
    <div id="file-tree">
        <ul> <!-- 根目录所在目录 -->
            <li> <!-- 根目录 -->
                <div class="file is-directory collapse" data-file-id="0" data-file-display-name="根目录" data-file-unique-name="" data-file-shared="">
                    <span class="branch"></span>    <!-- 根目录的箭头 -->
                    <span class="icon"></span>      <!-- 根目录的图标-->
                    <span class="name">根目录</span> <!-- 根目录的名字 -->
                </div>
                <ul></ul>
            </li>

            <li class="template"> <!-- 文件 (文件夹是特殊的文件) -->
                <div class="file" data-file-id="" data-file-display-name="" data-file-unique-name="" data-file-shared="">
                    <span class="branch"></span>    <!-- 文件夹的箭头 -->
                    <span class="icon"></span>      <!-- 图标-->
                    <span class="name">根目录</span> <!-- 名字 -->
                </div>
                <ul></ul> <!-- 文件夹会用到，如果是文件，没啥用，放在这不影响 -->
            </li>
        </ul>
    </div>

2. 引入需要的 CSS 和 JS
    <link rel="stylesheet" href="/lib/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="/lib/bootstrap-dialog.min.css">

    <link rel="stylesheet" href="/css/file.css">

    <script src="/lib/jquery.min.js"></script>
    <script src="/lib/jquery-ui.min.js"></script>
    <script src="/lib/bootstrap/js/bootstrap.min.js"></script>
    <script src="/lib/bootstrap-dialog.min.js"></script>

    <script src="/js/util.js"></script>
    <script src="/js/constants.js"></script>
    <script src="/js/filesystem/file-tree.js"></script>

3. 初始化 FileTree
    $(document).ready(function() {
        FileTree.init(FileSystemUrls.REST_FILE_SYSTEM_FILES);

        // 弹出显示在对话框中 [可选，依赖于 BootstrapDialog]
        $('#show-dialog-button').click(function() {
            FileTree.showInDialog();
        });

        // 接受拖放的数据 [可选]
        $('#drop-area').droppable({
            hoverClass: "drop-hover",
            drop: function(event, ui) {
                $('#drop-area').append('<div>{0}</div>'.format(ui.helper.attr('data-file-display-name')));
            }
        });
    });

4. 取得选中的 file id (文件或者目录的 ID)
    FileTree.getSelectedFileId();
*/

/**
 * 用于请求和显示目录树的结构，
 * 可以显示在页面中 id 为 file-tree 的 element 下，
 * 也可以调用 showInDialog() 显示在对话框里
 * 文件支持拖拽
 *
 * 使用:
 *     1. 初始化: FileTree.init(FileSystemUrls.LIST_FILES)，这个时候会显示到 #file-tree 上
 *     2. 调用: FileTree.showInDialog()，#file-tree 会显示到对话框中
 *
 * 注意: 目录是特殊的文件
 */
function FileTree() {
}

/**
 * 初始化 FileTree
 * @param url 请求文件的 URL
 * @param okCallback 点击确认按钮的回调函数
 */
FileTree.init = function(url, okCallback) {
    FileTree.url = url;
    FileTree.okCallback = okCallback;
    FileTree.fileLiTemplate = $('#file-tree > ul > li.template').remove().clone().removeClass('template');

    // 点击箭头展开或者收缩
    $('#file-tree').on('click', '.is-directory .branch', function(e) {
        e.stopPropagation();

        var $directory = $(this).parent();
        var directoryId = $directory.attr('data-file-id');

        if ($directory.hasClass('collapse')) {
            FileTree.expandDirectory(directoryId);
            FileTree.requestFiles(directoryId); // 请求文件
        } else {
            FileTree.collapseDirectory(directoryId);
        }
    });

    $(document).on('mousedown', '#file-tree .is-directory .branch', function(e) {
        e.stopPropagation(); // 不让点击事件传递到 parent
    });

    // 高亮点击的文件
    $(document).on('mousedown', '#file-tree .file', function(e) {
        e.stopPropagation(); // 不让点击事件传递到 parent

        $('#file-tree .file.active').removeClass('active');
        $(this).addClass('active');
    });
};

/**
 * 取得选中文件的 id
 */
FileTree.getSelectedFileId = function() {
    return $('#file-tree .file.active').attr('data-file-id');
};

/**
 * 在模态对话框里显示 FileTree，默认显示在 #file-tree 中
 */
FileTree.showInDialog = function() {
    // 重复使用对话框
    if (FileTree.dialog) {
        FileTree.dialog.open();
        return;
    }

    // 取消按钮
    var buttons = [{
            label: '取消',
            action: function(dialogRef){
                dialogRef.close();
            }
        }
    ];

    // 如果有确认的回调函数，显示确认按钮
    if (Utils.isFunctionExist(FileTree.okCallback)) {
        buttons.push({
            label: '确定',
            cssClass: 'btn-primary',
            action: function(dialogRef) {
                dialogRef.close();
                FileTree.okCallback();
            }
        });
    }

    FileTree.dialog = new BootstrapDialog({
        title: '文件系统',
        message: function() {
            return $('#file-tree').show();
        },
        closable: false,
        autodestroy: false,
        onshown: function() {
            $('.root-directory.collapse').click(); // 根目录是收缩的，则点击展开
            FileTree.enableDragFile(); // 解决第一次显示对话框时文件不能拖拽的 bug
        },
        buttons: buttons
    });

    FileTree.dialog.realize();
    FileTree.dialog.getModalHeader().hide();
    FileTree.dialog.open();
};

/**
 * 向服务器请求指定文件夹下的文件
 *
 * @param directoryId 文件夹的 id
 */
FileTree.requestFiles = function(directoryId) {
    var $clickedDirectoryUl = FileTree.findFileByFileId(directoryId).siblings('ul');

    Utils.restGet(FileTree.url.format({directoryId: directoryId}), {}, function(files) {
        // 没有文件或者子文件夹
        if (files.length === 0) { return; }

        for (var i = 0; i < files.length; ++i) {
            var file = files[i];

            // 如果已经存在，不再重复添加
            if (FileTree.findFileByFileId(file.fileId).length > 0) {
                continue;
            }

            // 创建文件的 element
            var $fileLi = FileTree.fileLiTemplate.clone();
            var $file = $fileLi.children('div.file');

            $file.attr('data-file-id', file.fileId);
            $file.attr('data-file-display-name', file.displayName);
            $file.attr('data-file-unique-name', file.uniqueName);
            $file.attr('data-file-shared', file.shared);
            $file.find('.name').text(file.displayName);
            $file.addClass(file.isFile ? 'is-file' : 'is-directory collapse');
            $file.addClass(Utils.getFileTypeClassName(file.uniqueName));

            $clickedDirectoryUl.append($fileLi);
        }

        FileTree.expandDirectory(directoryId);
        FileTree.enableDragFile();
    }, function(error) {
        Utils.showError(error.responseText);
    });
};

/**
 * 可以对文件进行拖拽，拖拽时隐藏对话框
 */
FileTree.enableDragFile = function() {
    $('#file-tree .is-file').draggable({
        helper: 'clone',
        cursor: 'move',
        zIndex: 10000,
        revert: 'invalid',
        appendTo: 'body',
        start: function(event, ui) {
            if (FileTree.dialog) {
                FileTree.dialog.close();
            }
        }
    });
};

/**
 * 从 #file-tree 下查找 id 为传入的 fileId，表示文件或者目录的 <a> 元素
 *
 * @return 表示文件的 jQuery 的 <a> 对象
 */
FileTree.findFileByFileId = function(fileId) {
    return $('#file-tree .file[data-file-id="{fileId}"]'.format({fileId: fileId}));
};

FileTree.expandDirectory = function(directoryId) {
    var $directory = FileTree.findFileByFileId(directoryId);
    $directory.siblings('ul').stop(true, true).fadeIn({ duration: 300, queue: false }).css('display', 'none').slideDown(300);
    $directory.removeClass('collapse').addClass('expand');
};

FileTree.collapseDirectory = function(directoryId) {
    var $directory = FileTree.findFileByFileId(directoryId);
    $directory.siblings('ul').stop(true, true).fadeOut({ duration: 300, queue: false }).slideUp(300);
    $directory.removeClass('expand').addClass('collapse');
};
