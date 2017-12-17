import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 知识点的树，节点的父子关系使用 node.code 建立。
 * 节点的 code 固定为 3 位的字符串，根节点为 000，
 * 父节点比子节点多一个或多个 0，以最近的优先，例如 110 是 112, 113 的父节点。
 */
public class KnowledgePointTree {
    List<Node> nodes = new LinkedList<>();

    public static void main(String[] args) {
        // 节点的顺序不重要
        KnowledgePointTree tree = new KnowledgePointTree();
        tree.addNode(new Node("000", "Root"));
        tree.addNode(new Node("100", "100"));
        tree.addNode(new Node("110", "110"));
        tree.addNode(new Node("111", "111"));
        tree.addNode(new Node("112", "112"));
        tree.addNode(new Node("113", "113"));
        tree.addNode(new Node("120", "120"));
        tree.addNode(new Node("121", "121"));
        tree.addNode(new Node("122", "122"));
        tree.addNode(new Node("123", "123"));
        tree.addNode(new Node("200", "200"));
        tree.addNode(new Node("210", "210"));
        tree.addNode(new Node("211", "211"));
        tree.addNode(new Node("212", "212"));
        tree.addNode(new Node("abc", "abc"));

        tree.build();
        tree.walk();
        // System.out.println(JSON.toJSONString(tree.nodes.get(0)));
    }

    /**
     * 添加节点
     *
     * @param node 树的节点
     */
    public void addNode(Node node) {
        if (node == null) { return; } // 忽略 null 节点

        nodes.add(node);
    }

    /**
     * 设置好节点数据后构建节点之间的父子关系
     */
    public void build() {
        for (Node node : nodes) {
            if (isRoot(node.code)) {
                continue;
            }

            Node parent = findParentNode(node.code);

            if (parent != null) {
                parent.children.add(node);
            }
        }
    }

    /**
     * 遍历树的所有节点
     */
    public void walk() {
        walk(nodes.get(0), 0);
    }

    private void walk(Node node, int index) {
        System.out.println(StringUtils.repeat(" ", index*3) + node.code); // 插入数据库

        List<Node> children = node.children;
        for (Node child : children) {
            walk(child, index+1);
        }
    }

    private Node findNode(String nodeCode) {
        for (Node node : nodes) {
            if (node.code.equals(nodeCode)) {
                return node;
            }
        }

        return null;
    }

    private Node findParentNode(String nodeCode) {
        do {
            String parentCode = getParentCode(nodeCode);
            Node parent = findNode(parentCode);

            if (parent != null) {
                return parent;
            }

            nodeCode = parentCode;
        } while (!isRoot(nodeCode));

        return null;
    }

    public static String getParentCode(String nodeCode) {
        char[] chs = nodeCode.toCharArray();

        for (int i = chs.length - 1; i >= 0; --i) {
            if (chs[i] != '0') {
                chs[i] = '0';
                break;
            }
        }

        return new String(chs);
    }

    public static boolean isRoot(String nodeCode) {
        return nodeCode.matches("0+");
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Node {
        String code;
        String name;
        List<Node> children = new LinkedList<>();

        Node(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}


