package btree;

import com.github.afkbrb.btp.BTPrinter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * 二叉树
 */
public class BTree {
    Node root; // 根节点

    /**
     * 使用整数数组构造一颗完全二叉树
     *
     * @param ns 整数数组
     */
    public static BTree build(int... ns) {
        Node[] nodes = new Node[ns.length];

        for (int i = 0; i < ns.length; i++) {
            nodes[i] = new Node(ns[i]);
        }

        for (int i = 0; i < nodes.length; i++) {
            int left  = 2 * i + 1;
            int right = 2 * i + 2;

            if (left < nodes.length) {
                nodes[i].left = nodes[left];
            }

            if (right < nodes.length) {
                nodes[i].right = nodes[right];
            }
        }

        BTree tree = new BTree();
        tree.root = nodes[0];
        return tree;
    }

    /**
     * 先序遍历
     */
    public void preorderTraversal() {
        BTree.preorderTraversal(this.root);
    }
    public static void preorderTraversal(Node node) {
        if (node == null) {  return; }

        BTree.visit(node);
        BTree.preorderTraversal(node.left);
        BTree.preorderTraversal(node.right);
    }

    /**
     * 中序遍历
     */
    public void inorderTraversal() {
        BTree.inorderTraversal(this.root);
    }
    public static void inorderTraversal(Node node) {
        if (node == null) {  return; }

        BTree.inorderTraversal(node.left);
        BTree.visit(node);
        BTree.inorderTraversal(node.right);
    }

    /**
     * 后序遍历
     */
    public void postorderTraversal() {
        BTree.postorderTraversal(this.root);
    }
    public static void postorderTraversal(Node node) {
        if (node == null) {  return; }

        BTree.postorderTraversal(node.left);
        BTree.postorderTraversal(node.right);
        BTree.visit(node);
    }

    /**
     * 先序遍历，使用非递归进行遍历
     */
    public void preorderTraversalUnrecursive() {
        Stack<Node> stack = new Stack<>();
        stack.push(this.root);

        while (!stack.isEmpty()) {
            Node node = stack.pop();
            BTree.visit(node);

            if (node.right != null) {
                stack.push(node.right); // 后访问，先入栈
            }
            if (node.left != null) {
                stack.push(node.left);
            }
        }
    }

    /**
     * 中序遍历，使用非递归进行遍历
     */
    public void inorderTraversalUnrecursive() {
        Stack<Node> stack = new Stack<>();
        Node node = this.root;

        while (node != null || !stack.isEmpty()) {
            // node 到它最左端的节点入栈
            while (node != null) {
                stack.push(node);
                node = node.left;
            }

            node = stack.pop();
            BTree.visit(node);

            // node 为其右子树
            node = node.right;
        }
    }

    /**
     * 计算树的高度
     *
     * @return 返回树的高度
     */
    public int height() {
        return BTree.height(this.root);
    }

    /**
     * 计算以传入的节点 node 为根的子树的高度
     *
     * @param node 子树的根节点
     * @return 返回子树的高度
     */
    public static int height(Node node) {
        if (node == null) {
            return 0;
        }

        // 当前节点的高度: 左子树和右子树的最大高度 + 1
        return Math.max(BTree.height(node.left), BTree.height(node.right)) + 1;
    }

    /**
     * 层次遍历，使用广度优先算法
     */
    public void breadthFirstTraversal() {
        BTree.breadthFirstTraversal(this.root);
    }

    public static void breadthFirstTraversal(Node root) {
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            BTree.visit(node);

            if (node.left != null) {
                queue.offer(node.left);
            }

            if (node.right != null) {
                queue.offer(node.right);
            }
        }
    }

    /**
     * 访问节点
     *
     * @param node 树节点
     */
    public static void visit(Node node) {
        if (node != null) {
            System.out.print(node.data + ", ");
        }
    }

    /**
     * 返回完全二叉树的字符串表示
     */
    public static String toPerfectBinaryTree(Node root) {
        StringBuilder sb = new StringBuilder();
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            Node node = queue.poll();

            if (node == null) {
                sb.append("#").append(",");
                continue;
            }

            sb.append(node.data).append(",");

            queue.offer(node.left);
            queue.offer(node.right);
        }

        return sb.toString().replaceAll("(#,)+$", "").replaceAll(",$", "");
    }

    /**
     * 打印树
     *
     * @param root 树的根节点
     */
    public static void print(Node root) {
        BTPrinter.printTree(BTree.toPerfectBinaryTree(root));
        System.out.println();
    }

    public static void main(String[] args) {
        /*
                        1
                    2       3
                 4    5   6   7
               8   9
         */
        BTree tree = BTree.build(1, 2, 3, 4, 5, 6, 7, 8, 9);
        BTree.print(tree.root);

        System.out.println("先序遍历:");
        tree.preorderTraversal(); // 1, 2, 4, 8, 9, 5, 3, 6, 7

        System.out.println("\n\n先序遍历 (非递归):");
        tree.preorderTraversalUnrecursive(); // 1, 2, 4, 8, 9, 5, 3, 6, 7

        System.out.println("\n\n中序遍历:");
        tree.inorderTraversal(); // 8, 4, 9, 2, 5, 1, 6, 3, 7

        System.out.println("\n\n中序遍历 (非递归):");
        tree.inorderTraversalUnrecursive(); // 8, 4, 9, 2, 5, 1, 6, 3, 7

        System.out.println("\n\n后续遍历:");
        tree.postorderTraversal(); // 8, 9, 4, 5, 2, 6, 7, 3, 1

        System.out.println("\n\n层次遍历:");
        tree.breadthFirstTraversal(); // 1, 2, 3, 4, 5, 6, 7, 8, 9

        System.out.println("\n\n树的高度:");
        System.out.println(tree.height()); // 4
        System.out.println(BTree.height(tree.root.left)); // 3
        System.out.println(BTree.height(tree.root.right)); // 2
    }
}
