package btree;

import com.github.afkbrb.btp.BTPrinter;

/**
 * 二叉搜索树、二叉排序树，中序遍历为升序排列
 */
public class BSTree {
    Node root;

    /**
     * 用整数数组构建一颗二叉排序树
     *
     * @param ns 整数数组
     * @return 返回二叉排序树
     */
    public static BSTree build(int... ns) {
        BSTree tree = new BSTree();

        for (int n : ns) {
            tree.insert(n);
        }

        return tree;
    }

    /**
     * 查找值为传入的 data 的节点
     *
     * @param data 要查找的值
     * @return 返回查找到的节点
     */
    public Node find(int data) {
        Node node = root;

        while (node != null) {
            // 小于从左子树找
            // 大于从右子树找
            // 等于则找到
            if (data < node.data) {
                node = node.left;
            } else if (data > node.data) {
                node = node.right;
            } else {
                return node;
            }
        }

        return null;
    }

    /**
     * 查找值为传入的 data 的节点的父节点
     *
     * @param data 要查找的值
     * @return 返回查找到的父节点
     */
    public Node findParent(int data) {
        Node parent = null;
        Node node = root;

        while (node != null) {
            if (data < node.data) {
                parent = node;
                node = node.left;
            } else if (data > node.data){
                parent = node;
                node = node.right;
            } else {
                return parent;
            }
        }

        return null;
    }

    /**
     * 二叉排序树插入节点
     *
     * @param data 要插入的值
     */
    public void insert(int data) {
        Node target = new Node(data);

        if (root == null) {
            root = target;
        } else {
            Node node = root;

            while (true) {
                if (data < node.data) {
                    if (node.left == null) {
                        node.left = target;
                        break;
                    }
                    node = node.left;
                } else {
                    if (node.right == null) {
                        node.right = target;
                        break;
                    }
                    node = node.right;
                }
            }
        }
    }

    /**
     * 二叉排序树插入节点: 递归插入
     *
     * @param data 要插入的值
     */
    public void insertRecursive(int data) {
        insertRecursive(root, data);
    }

    private void insertRecursive(Node node, int data) {
        if (root == null) {
            root = new Node(data);
            return;
        }

        if (data < node.data) {
            // 小于放在左子树
            if (node.left == null) {
                node.left = new Node(data);
            } else {
                insertRecursive(node.left, data);
            }
        } else {
            // 大于等于放在右子树
            if (node.right == null) {
                node.right = new Node(data);
            } else {
                insertRecursive(node.right, data);
            }
        }
    }

    /**
     * 删除值为 data 的节点
     *
     * @param data 要删除的值
     */
    public void delete(int data) {
        // 1. 删除的节点为根节点
        //    root.left == null: root = root.right
        //    root.right == null: root == root.left
        //    root.left 和 root.right 都不为 null, 按 5 删除同时有左右子树的节点进行删除
        // 2. 删除叶节点 (3, 6): 父节点的 left or right 为 null
        // 3. 删除只有左子树的节点 (9): 父节点的 left or right 为它左子树
        // 4. 删除只有右子树的节点 (2, 11): 父节点的 left or right 为它右子树
        // 5. 删除同时有左子树和右子树的节点 (10, 7):
        //    从左子树中找到值最大的节点 max (前驱)
        //    node 的值为 max 的值
        //    max 的父节点的 left or right 为 max.left (因为 max 的 right 一定为 null)
        //    删除 max (没有引用指向)

        Node node = find(data);
        Node parent = findParent(data);

        // 被删除节点不存在
        if (node == null) {
            return;
        }

        // [1] 删除的节点为根节点
        if (node == root && root.left == null) {
            root = root.right;
            return;
        }
        if (node == root && root.right == null) {
            root = root.left;
            return;
        }

        // 提示: 叶子节点是左孩子或者右孩子为空的特例
        if (node.right == null) {
            // [3] 删除只有左子树的节点
            if (parent.left == node) {
                parent.left = node.left;
            } else {
                parent.right = node.left;
            }
        } else if (node.left == null) {
            // [4] 删除只有右子树的节点
            if (parent.left == node) {
                parent.left = node.right;
            } else {
                parent.right = node.right;
            }
        } else {
            // [5] 删除同时有左子树和右子树的节点
            Node max = node.left;  // 值最大的节点 (max.right 肯定为 null)
            Node maxParent = node; // 值最大的节点的父节点

            // 从左子树中找到值最大的节点 max (前驱)
            while (max.right != null) {
                maxParent = max;
                max = max.right;
            }

            // node 的值为 max 的值
            node.data = max.data;

            // if (maxParent.left == max)
            if (maxParent == node) {
                // node 的左孩子就是最大值的节点
                maxParent.left = max.left;
            } else {
                maxParent.right = max.left;
            }
        }
    }

    public static void main(String[] args) {
        // 1. 构造排序二叉树
        BSTree tree = BSTree.build(5, 4, 2, 3, 10, 7, 6, 9, 8, 13, 11, 12);
        System.out.print("中序遍历: ");
        BTree.inorderTraversal(tree.root); // 中序遍历得到升序序列
        System.out.println();

        System.out.println("树的结构:");
        BTree.print(tree.root);

        int[] dataList = { 7, 10, 5, 11, 8, 2, 13 };
        for (int data : dataList) {
            System.out.println("删除 " + data + ":");
            tree.delete(data);
            BTree.print(tree.root);
        }
    }
}
