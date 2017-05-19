// DnD(Drag and Drop) 是拖拽实现的核心
DnD = {
    $draggedElement: null, // 正在拖拽的 element
    // 按下鼠标
    mouseDown: function(e) {
        var $doc = $(document);
        var x = $doc.scrollLeft();
        var y = $doc.scrollTop();

        // 创建并保存拖拽的 element
        DnD.$draggedElement = $(e.target).clone();
        DnD.$draggedElement.addClass('dragged-item');
        DnD.$draggedElement.css({
            'position': 'absolute',
            'left': (e.clientX + x + 2) + 'px',
            'top' : (e.clientY + y + 2) + 'px'
        });
        DnD.$draggedElement.appendTo('body');

        // 绑定鼠标移动，松开和选择文本事件
        $doc.on('mousemove',   DnD.mouseMove);
        $doc.on('mouseup',     DnD.mouseUp);
        $doc.on('selectstart', DnD.noSelect);

        return false;
    },
    // 移动鼠标
    mouseMove: function(e) {
        // 移动拖拽的 element
        var $doc = $(document);
        var x = $doc.scrollLeft();
        var y = $doc.scrollTop();

        DnD.$draggedElement.css({
            'left': (e.clientX + x + 3) + 'px',
            'top' : (e.clientY + y + 3) + 'px'
        });

        return false;
    },
    // 松开鼠标
    mouseUp: function(e) {
        // 松开鼠标时取消 document 绑定的事件
        var $doc = $(document);
        $doc.off('mousemove',   DnD.mouseMove);
        $doc.off('mouseup',     DnD.mouseUp);
        $doc.off('selectstart', DnD.noSelect);

        // 删除拖拽的 element
        DnD.$draggedElement.remove();
        DnD.$draggedElement = null;
    },
    // 取消选择，为了实现效果好一些，所以在拖拽时不允许选择文本
    noSelect: function() {
        return false;
    }
};
