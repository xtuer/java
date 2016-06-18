
function FileUploader() {

}

FileUploader.initFileUpload = function(uploadUrl, successCallback) {
    $('#fileupload').fileupload({
        url: uploadUrl,
        dataType: 'json',
        maxFileSize: 50000000, // 允许上传的文件的最大大小 (50M)
        // acceptFileTypes: /(\.|\/)(gif|jpe?g|png|rar|zip)$/i, // 允许上传的文件类型
        // dropZone: $('#dropzone'), // 限制拖拽到 dropzone 里才能上传文件, 如果没有这个参数, 拖拽到浏览器里任何地方就上传了
        progressInterval: 10,
        change: function(e, data) {
            // 限制文件选择窗口中只能选择上传一个文件
            if(data.files.length > 1){
                alert("Max 1 files are allowed");
                return false;
            }
        },
        drop: function(e, data) {
            // 限制只能拖拽上传一个文件
            if(data.files.length > 1){
                alert("Max 1 files are allowed");
                return false;
            }
        },
        progress: function (e, data) {
            $.each(data.files, function(index, file) {
                var progress = parseInt(data.loaded / data.total * 100, 10);
                FileUploader.updateProgressBar(file.name, progress);
            });
        },
        processfail: function(e, data) {
            alert(data.files[0].error); // 上传的文件验证不通过, 显示错误信息
        },
        processdone: function (e, data) {
            // 验证通过，开始上传时创建一个进度条
            $.each(data.files, function (index, file) {
                FileUploader.createProgressBar(file.name);
            });
        },
        done: function(e, data) {
            var filesDirectories = data._response.result;
            successCallback(filesDirectories);
        }
    });

    FileUploader.enableDragAndDropEffect(); // 可选
// });
};

// 添加文件时创建进度条
FileUploader.createProgressBar = function(filename) {
    var $progress = $('#progresses .template').clone().removeClass('template');
    var $progressBar = $progress.find('.progress-bar');
    $progress.attr('data-file-name', filename);
    $progressBar.text(filename + ' 0%');
    $progress.appendTo($('#progresses'));
};

// 更新进度条
FileUploader.updateProgressBar = function(filename, progress) {
    var $progress = $('#progresses .progress[data-file-name="' + filename + '"]');
    var $progressBar = $progress.find('.progress-bar');
    $progressBar.css('width', progress + '%').text(filename + ' ' + progress + '%');
};

// 当拖拽文件到 dropzone 上时给其添加效果
FileUploader.enableDragAndDropEffect = function() {
    $('#dropzone').on('dragover', function(e){
        $('#dropzone').addClass('hover');
    }).on('dragleave', function(e) {
        $('#dropzone').removeClass('hover');
    }).on('drop', function() {
        $('#dropzone').removeClass('hover');
    });
};
