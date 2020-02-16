package btree;

/**
 * 二叉树的节点
 */
public class Node {
    int  data;  // 数据
    Node left;  // 左孩子
    Node right; // 右孩子

    // 下面 2 个属性在线索二叉树中使用
    boolean leftThreaded;  // true 表示左孩子线索化, left 指向前驱，false 表示 left 为左子树
    boolean rightThreaded; // true 表示右孩子线索化, right 指向后继, false 表示 right 为右子树

    public Node(int data) {
        this.data = data;
    }
}
