// DnD(Drag and Drop) 是拖拽实现的核心
DnD = {
    startX: 0,
    startY: 0,
    isDragging: false,
    $draggedElement: null, // 正在拖拽的 element
    // 按下鼠标
    mouseDown: function(e) {
        var $doc = $(document);

        DnD.startX = e.clientX + $doc.scrollLeft();
        DnD.startY = e.clientY + $doc.scrollTop();
        DnD.isDragging = false;

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
        var x = e.clientX + $doc.scrollLeft();
        var y = e.clientY + $doc.scrollTop();

        if (!DnD.isDragging) {
            var dx = x - DnD.startX;
            var dy = y - DnD.startY;

            // 非拖拽模式下，当离开鼠标按下位置的 Manhattan length 大于 4 个像素时开始拖拽
            if (Math.abs(dx) + Math.abs(dy) > 4) {
                DnD.isDragging = true;

                // 创建并保存拖拽的 element
                DnD.$draggedElement = $(e.target).clone();
                DnD.$draggedElement.addClass('dragged-item');
                DnD.$draggedElement.css({
                    'position': 'absolute',
                    'left': (x + 2) + 'px',
                    'top' : (y + 2) + 'px'
                });
                DnD.$draggedElement.appendTo('body');
            }
        } else {
            // 移动拖拽的元素
            DnD.$draggedElement.css({
                'left': (x + 2) + 'px',
                'top' : (y + 2) + 'px'
            });
        }

        return false;
    },
    // 松开鼠标
    mouseUp: function(e) {
        DnD.isDragging = false;

        // 松开鼠标时取消 document 绑定的事件
        var $doc = $(document);
        $doc.off('mousemove',   DnD.mouseMove);
        $doc.off('mouseup',     DnD.mouseUp);
        $doc.off('selectstart', DnD.noSelect);

        // 删除拖拽的 element
        if (DnD.$draggedElement) {
            DnD.$draggedElement.remove();
            DnD.$draggedElement = null;
        }
    },
    // 取消选择，为了实现效果好一些，所以在拖拽时不允许选择文本
    noSelect: function() {
        return false;
    }
};
