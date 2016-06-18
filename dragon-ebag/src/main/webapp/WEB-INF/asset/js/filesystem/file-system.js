// 注:
// 文件夹是特殊的文件
// 目录和文件夹是指同一个东西，所以有的时候用目录，有的时候用文件夹，表示的都是同样的东西

$(document).ready(function() {
    FileTree.init(FileSystemUrls.REST_FILE_SYSTEM_SUBDIRECTORIES, FileSystem.moveFileToSelectedDirectory);

    File.fileRowTemplate = $('#file-flat .list-group-item.template').remove(); // 文件的模版
    FileSystem.requestFiles(0); // 获取用户根目录下的文件和文件夹

    // 创建目录
    $('#create-directory').click(function() {
        FileSystem.createDirectory(FileSystem.getCurrentDirectoryId(), '新建文件夹');
    });

    // 点击目录，添加到目录路径中，并请求此目录下的文件
    $(document).on('click', '#file-flat .is-directory', function() {
        var directoryId = $(this).attr('data-file-id');
        var displayName = $(this).attr('data-file-display-name');

        var $dir = $('#directory-path .breadcrumb li:first').clone(); // 利用根目录节点来创建新的目录节点
        $dir.find('a').attr('data-directory-id', directoryId).text(displayName);
        $dir.appendTo($('#directory-path .breadcrumb'));

        FileSystem.requestFiles(directoryId);
    });

    // 点击文件进行预览
    $(document).on('click', '#file-flat .is-file', function(e) {

    });

    // 点击路径中非最后一个文件夹，删除被点中的目录后面的所有目录节点，并请求此目录下的文件
    $(document).on('click', '#directory-path .breadcrumb li a:not(:last)', function() {
        var directoryId = $(this).attr('data-directory-id');
        $(this).parent().nextAll().remove(); // 删除被点中的目录后面的所有目录节点
        FileSystem.requestFiles(directoryId);
    });

    // 鼠标进入文件所在行，显示工具栏
    $(document).on('mouseenter', '#file-flat li.list-group-item', function() {
        var fileId = $(this).find('.file').attr('data-file-id');
        FileSystem.showDropdown(fileId);
    });

    // 点击 '移动到' 按钮，弹出文件夹选择窗口
    $('#move').click(function() {
        FileTree.showInDialog();
    });

    // 删除文件或者文件夹
    $('#delete').click(function() {
        BootstrapDialog.confirm({
            title: '注意',
            message: '确定要删除吗？',
            type: BootstrapDialog.TYPE_DANGER,
            btnCancelLabel: '取消',
            btnOKLabel: '确定',
            callback: function(result) {
                if(result) {
                    var fileId = FileSystem.getFileIdWhichHoldsDropdown();
                    FileSystem.deleteFile(fileId);
                }
            }
        });
    });

    // 显示重命名编辑工具
    $('#rename').click(function() {
        var fileId = FileSystem.getFileIdWhichHoldsDropdown();
        FileSystem.showRenameEdit(fileId);
    });

    // 防止重命名时点击进入目录
    $('#rename-edit').click(function(e) {
        e.stopPropagation();
    });

    // 隐藏重命名编辑工具
    $('#rename-edit .close-button').click(function(e) {
        FileSystem.hideRenameEdit();
    });

    // 点击提交重命名
    $('#rename-edit .submit-button').click(function(e) {
        FileSystem.submitRename();
    });

    // 重命名编辑器的键盘事件
    $('#rename-edit input').on('keypress', function(e) {
        if('13' == e.keyCode) {
            // 按下回车提交重命名
            FileSystem.submitRename();
        } else if ('27' == e.keyCode) {
            // 按下 ESC 取消重命名
            FileSystem.hideRenameEdit();
        }
    });

    $('#share').click(function() {
        var fileId = FileSystem.getFileIdWhichHoldsDropdown();
        FileSystem.shareFile(fileId, true);
    });

    $('#unshare').click(function() {
        var fileId = FileSystem.getFileIdWhichHoldsDropdown();
        FileSystem.shareFile(fileId, false);
    });
});

/**
 * 表示文件的结构。
 *
 * @param fileId 文件的 id
 * @param displayName 文件显示到界面上的名字
 * @param uniqueName 文件唯一的名字
 * @param shared 文件是否为可共享
 * @param ui 表示文件的 <a> 元素，例如
        <a href="javascript:void(0);" class="file is-directory" data-file-id="277" data-file-display-name="科目" data-file-unique-name="" data-file-shared="false">
            <span class="display-name">科目</span>
        </a>
 */
function File(fileId, displayName, uniqueName, shared, ui) {
    this.fileId = fileId || -1;
    this.displayName = displayName || '';
    this.uniqueName = uniqueName || '';
    this.shared = shared || false;
    this.ui = ui || null;
}

File.prototype.showDisplayName = function() {
    this.ui.find('.name').show();
};

File.prototype.hideDisplayName = function() {
    this.ui.find('.name').hide();
};

/**
 * 当文件的属性(不包含 ui) 如 displayName 变化后需要更新到界面上时调用这个方法。
 */
File.prototype.updateInUi = function() {
    this.ui.attr('data-file-id', this.fileId);
    this.ui.attr('data-file-display-name', this.displayName);
    this.ui.attr('data-file-unique-name', this.uniqueName);
    this.ui.attr('data-file-shared', this.shared);
    this.ui.find('.name').text(this.displayName);

    if (this.shared) {
        this.ui.addClass('shared');
    } else {
        this.ui.removeClass('shared');
    }
};

/**
 * 从界面上删除文件。
 * 表示文件的 <div> 是放在一个 <li> 里的，所以从界面上删除文件时需要删除它的 parent 从界面上删除文件。
 */
File.prototype.deleteFromUi = function() {
    FileSystem.hideDropdown();
    FileSystem.hideRenameEdit();
    this.ui.parent().remove();
};

/**
 * 使用文件的 id 找到它对应的 file
 *
 * @return File 对象
 */
File.findFileFromUi = function(fileId) {
    var $file = $('#file-flat .file[data-file-id="{fileId}"]'.format({fileId: fileId}));
    return new File(fileId,
        $file.attr('data-file-display-name'),
        $file.attr('data-file-unique-name'),
        $file.attr('data-file-shared') == 'true',
        $file);
};

/**
 * 创建表示文件的 li 元素。
 *
 * @param fileId 文件的 id
 * @param displayName 文件显示的名字
 * @param uniqueName 文件唯一的名字
 * @param type 为 true 时表示是普通文件，为 false 时表示文件为文件夹
 * @param shared 为 true 时表示文件是共享的，为 false 时文件为非共享状态
 * @return 表示文件的 li 元素
 */
File.createFileRowUi = function(fileId, displayName, uniqueName, type, shared) {
    var $fileRow = File.fileRowTemplate.clone().removeClass('template');
    var $file = $fileRow.find('.file');

    $file.attr('data-file-id', fileId);
    $file.attr('data-file-display-name', displayName);
    $file.attr('data-file-unique-name', uniqueName);
    $file.attr('data-file-shared', shared);
    $file.find('.name').text(displayName);

    // 根据文件的类型添加样式
    $file.addClass(shared ? 'shared': '');
    $file.addClass((type !== 0) ? 'is-file': 'is-directory');
    $file.addClass(Utils.getFileTypeClassName(uniqueName));

    return $fileRow;
};

/**
 * 添加文件和文件夹到界面上。
 *
 * @param files 文件或者文件夹的数组
 */
File.appendFilesToUi = function(files) {
    $.each(files, function(index, file) {
        File.createFileRowUi(file.fileId, file.displayName, file.uniqueName, file.type, file.shared).appendTo($('#file-flat'));
    });

    FileSystem.enableDragAndDrop();
};

/**
 * 清空界面上显示的文件。
 */
File.deleteAllFilesFromUi = function() {
    FileSystem.hideDropdown();   // 隐藏工具栏
    FileSystem.hideRenameEdit(); // 隐藏重命名编辑器
    $('#file-flat .list-group-item:not(.template)').remove();
};

/**
 * 文件操作的类，上传文件，创建文件夹，修改文件和文件夹的名字，分享等
 * 注：文件夹也是文件
 */
function FileSystem() {

}

/**
 * 从文件夹路径中取得当前目录的 id。
 *
 * @return 当前文件夹的 id
 */
FileSystem.getCurrentDirectoryId = function() {
    return $('#directory-path .breadcrumb li a:last').attr('data-directory-id');
};

/**
 * 创建目录，并显示重命名编辑器。
 *
 * @param parentDirectoryId 父文件夹的 id
 * @param directoryName 新建文件夹的名字
 */
FileSystem.createDirectory = function(parentDirectoryId, directoryName) {
    Utils.restCreate(FileSystemUrls.REST_FILE_SYSTEM_DIRECTORIES,
        {directoryName: directoryName, parentDirectoryId: parentDirectoryId}, function(result) {
            if (result.success) {
                var directories = [result.data];
                File.appendFilesToUi(directories);
                FileSystem.showRenameEdit(result.data.fileId);
            } else {
                BootstrapDialog.show({title: '错误', message: result.message, type: BootstrapDialog.TYPE_DANGER});
            }
        }, function(response) {
            console.log('error');
        }
    );
};

/**
 * 向服务器请求 id 为 directoryId 的文件夹下的文件和文件夹。
 *
 * @param directoryId 文件夹的 id
 */
FileSystem.requestFiles = function(directoryId) {
    // 更新上传插件上传文件时的目录
    FileUploader.initFileUpload(FileSystemUrls.FILE_SYSTEM_FILE_UPLOAD_COMMON.format({directoryId: directoryId}), File.appendFilesToUi);

    /* 从服务器得到的数据格式为
    [{
        "fileId": 198,
        "displayName": "地理",
        "uniqueName": "",
        "type": 0,
        "directoryId": 277,
        "userId": 10,
        "shared": false,
        "external": false,
        "markAsDeleted": false,
        "extension": ""
    }]*/
    $.getJSON(FileSystemUrls.REST_FILE_SYSTEM_FILES.format({directoryId: directoryId}),
        function(files) {
            File.deleteAllFilesFromUi(); // 清楚界面上的文件
            File.appendFilesToUi(files); // 显示新请求到的文件
        }
    );
};

/**
 * 启用拖拽文件操作，例如移动文件到其他文件夹时很方便。
 */
FileSystem.enableDragAndDrop = function() {
    $('#file-flat .file').draggable({
        helper: 'clone',
        cursor: 'move',
        zIndex: 10000,
        revert: 'invalid' // 拖拽失败时漂回原来的位置
    });

    $('#file-flat .is-directory').droppable({
        hoverClass: "drop-hover",
        drop: function(event, ui) {
            $.each(ui.draggable, function(index, file) {
                var $file = $(this);
                var $directory = $(event.target);
                var fileId = $file.attr('data-file-id');
                var directoryId = $directory.attr('data-file-id');
                FileSystem.moveFileToDirectory(fileId, directoryId);
            });
        }
    });
};

/**
 * 删除文件或者文件夹
 *
 * @param fileId 文件的 id
 */
FileSystem.deleteFile = function(fileId) {
    Utils.restDelete(FileSystemUrls.REST_FILE_SYSTEM_FILES_WITH_ID.format({fileId: fileId}), {}, function(result) {
        if (result.success) {
            File.findFileFromUi(fileId).deleteFromUi();
        } else {
            Utils.showError(result.message);
        }
    }, function(error) {
        Utils.showError(error.responseText);
    });
};

/**
 * 移动文件到选中的文件夹中(在弹出框里选择移动的目标文件夹)。
 */
FileSystem.moveFileToSelectedDirectory = function() {
    var fileId = FileSystem.getFileIdWhichHoldsDropdown();
    var directoryId = FileTree.getSelectedFileId();
    FileSystem.moveFileToDirectory(fileId, directoryId);
};

/**
 * 移动 id 为 fileId 的文件到 id 为 directoryId 的文件夹
 *
 * @param fileId 文件的 id
 * @param directoryId 文件夹的 id
 */
FileSystem.moveFileToDirectory = function(fileId, directoryId) {
    var data = {
        action: 'MOVE',
        directoryId: directoryId
    };

    Utils.restUpdate(FileSystemUrls.REST_FILE_SYSTEM_FILES_WITH_ID.format({fileId: fileId}), data, function(result) {
        // 从文件列表里删除
        if (result.success) {
            File.findFileFromUi(fileId).deleteFromUi();
        } else {
            Utils.showError(result.message);
        }
    }, function(error) {
        Utils.showError(error.responseText);
    });
};

/**
 * 共享或者取消文件共享。
 *
 * @param fileId 文件的 id
 * @param shared 为 true 时设置文件为共享，为 false 时取消共享
 */
FileSystem.shareFile = function(fileId, shared) {
    var data = {
        action: 'SHARE',
        shared: shared
    };

    Utils.restUpdate(FileSystemUrls.REST_FILE_SYSTEM_FILES_WITH_ID.format({fileId: fileId}), data, function(result) {
        if (result.success) {
            var file = File.findFileFromUi(fileId);
            file.shared = shared;
            file.updateInUi();
        } else {
            Utils.showError(result.message);
        }
    }, function(error) {
        Utils.showError(error.responseText);
    });
};

/**
 * 提交修改的 displayName，不会修改文件的 uniqueName。
 */
FileSystem.submitRename = function() {
    var $input = $('#rename-edit input');
    var newDisplayName = $.trim($input.val());

    if (!newDisplayName) {
        Utils.showError('文件名不能为空');
        return;
    }

    var fileId = $('#rename-edit').siblings('.file').attr('data-file-id');
    FileSystem.renameFile(fileId, newDisplayName);
};

/**
 * 修改文件的显示文件名，不会修改文件唯一的文件名
 *
 * @param 文件的 id
 * @param displayName 新的显示用的文件名
 */
FileSystem.renameFile = function(fileId, displayName) {
    var data = {
        action: 'RENAME',
        displayName: displayName
    };

    Utils.restUpdate(FileSystemUrls.REST_FILE_SYSTEM_FILES_WITH_ID.format({fileId: fileId}), data, function(result) {
        if (result.success) {
            // 修改 displayName 并显示
            var file = File.findFileFromUi(fileId);
            file.displayName = displayName;
            file.updateInUi();
            file.showDisplayName();

            // 隐藏重命名工具
            FileSystem.hideRenameEdit();
        } else {
            Utils.showError(result.message);
        }
    }, function(error) {
        Utils.showError(error.responseText);
    });
};

/**
 * 显示下拉菜单到 id 为 fileId 的文件所在行。
 *
 * @param fileId 文件的 id
 */
FileSystem.showDropdown = function(fileId) {
    if ($('#rename').is(':hidden')) { // 下拉菜单显示的时候不能显示到其他文件所在行
        var file = File.findFileFromUi(fileId);
        $('#dropdown').appendTo(file.ui.parent()).show(function() {
            if (file.shared) {
                $('#share').hide();
                $('#unshare').show();
            } else {
                $('#share').show();
                $('#unshare').hide();
            }
        });
    }
};

/**
 * 隐藏下拉菜单并移到 body。
 */
FileSystem.hideDropdown = function() {
    $('#dropdown').hide().appendTo($('body'));
};

/**
 * 取得下拉菜单所在文件行的 fileId。
 *
 * @return 文件的 id
 */
FileSystem.getFileIdWhichHoldsDropdown = function() {
    return $('#dropdown').closest('.list-group-item').find('.file').attr('data-file-id');
};

/**
 * 显示重命名编辑器。
 *
 * @param fileId 文件的 id
 */
FileSystem.showRenameEdit = function(fileId) {
    var $renameEdit = $('#rename-edit');

    // 显示的时候进行显示，说明被移动到其他文件元素上进行重命名了，则要显示上一个重命名的文件名
    if ($renameEdit.is(':visible')) {
        $renameEdit.siblings('.name').show();
    }

    var file = File.findFileFromUi(fileId);
    var filename = file.displayName;
    file.hideDisplayName(); // 隐藏名字

    $renameEdit.find('input').val(filename);
    $renameEdit.appendTo(file.ui.parent());
    $renameEdit.show();  // 显示编辑工具

    // 选中文件名后缀前的部分
    var lastDotIndex = filename.lastIndexOf('.');
    lastDotIndex = (lastDotIndex != -1) ? lastDotIndex : filename.length;
    Utils.selectInputText($renameEdit.find('input')[0], 0, lastDotIndex);
};

/**
 * 隐藏重命名编辑器。
 */
FileSystem.hideRenameEdit = function() {
    var $renameEdit = $('#rename-edit');
    $renameEdit.hide();
    $renameEdit.siblings('.file').find('.name').show();
    $renameEdit.appendTo($('body'));
};
