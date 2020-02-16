package btree;

/**
 * 线索二叉树 (threaded)
 */
public class TBTree {
    Node root;

    public TBTree() {
        this.root = BTree.build(1, 2, 3, 4, 5, 6, 7, 8, 9).root;
        this.inorderThreading(this.root);
    }

    // 线索化二叉树时的前一个节点
    private Node prev = null;

    /**
     * 线索化二叉树 (中序遍历)
     */
    private void inorderThreading(Node node) {
        // 1. 线索化左子树
        // 2. 线索化当前节点: 访问节点的时候才执行线索化
        // 3. 线索化右子树

        if (node == null) { return; }

        // [1] 线索化左子树
        this.inorderThreading(node.left);

        // [2] 线索化当前节点: 访问节点的时候才执行线索化
        // 前一个节点 prev 的右孩子为 null，设置它的后继为当前节点
        if (prev != null && prev.right == null) {
            prev.right = node;
            prev.rightThreaded = true;
        }

        // 当前节点 node 的左孩子为 null，则设置当前节点的前驱为 prev
        if (node.left == null) {
            node.left = prev;
            node.leftThreaded = true;
            prev = node;
        }

        // [3] 线索化右子树
        this.inorderThreading(node.right);
    }

    /**
     * 使用线索遍历二叉树: 不再需要递归
     */
    public void inorderTraversalThreaded() {
        // 1. 最后一个访问的节点的右子树为空，作为结束循环的条件
        // 2. 找到最左边非线索化的节点 node，访问 node
        // 3. 沿着 node 的后继链访问所有能达到的节点
        // 5. 当没有后继可用时，访问右孩子 (中序访问为左中右)

        Node node = root;

        // [1] 最后一个访问的节点的右子树为空，作为结束循环的条件
        while (node != null) {
            // [2] 找到最左边非线索化的节点 node，访问 node
            while (!node.leftThreaded && node.left != null) {
                node = node.left;
            }

            BTree.visit(node);

            // [3] 沿着 node 的后继链访问所有能达到的节点
            while (node.rightThreaded) {
                node = node.right;
                BTree.visit(node);
            }

            // [5] 当没有后继可用时，访问右孩子 (中序访问为左中右)
            node = node.right;
        }
    }

    public static void main(String[] args) {
        TBTree tree = new TBTree();
        tree.inorderTraversalThreaded(); // 8, 4, 9, 2, 5, 1, 6, 3, 7
    }
}
