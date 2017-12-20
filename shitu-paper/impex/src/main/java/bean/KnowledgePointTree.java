package bean;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import util.SnowflakeIdWorker;

import java.util.LinkedList;
import java.util.List;

/**
 * 知识点的树，节点的父子关系使用 node.code 建立。
 * 节点的顺序不重要，节点的 code 固定为 3 位的字符串，根节点为 000，
 * 父节点比子节点多一个或多个 0，以最近的优先，例如 110 是 112, 113 的父节点。
 */

@Getter
@Setter
@Accessors(chain = true)
public class KnowledgePointTree {
    List<KnowledgePoint> nodes = new LinkedList<>();
    SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

    /**
     * 添加节点
     *
     * @param node 树的节点
     */
    public void addNode(KnowledgePoint node) {
        if (node == null) { return; } // 忽略 null 节点

        nodes.add(node);
    }

    /**
     * 获取知识点树的根节点
     *
     * @return 返回根节点
     */
    public KnowledgePoint getRoot() {
        return nodes.get(0);
    }

    /**
     * 设置好节点数据后构建节点之间的父子关系
     */
    public void build() {
        for (KnowledgePoint node : nodes) {
            if (isRoot(node.code)) {
                continue;
            }

            KnowledgePoint parent = findParentNode(node.code);

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

    private void walk(KnowledgePoint node, int index) {
        System.out.println(StringUtils.repeat(" ", index*3) + node.code + "-" + node.name); // 插入数据库

        node.id = idWorker.nextId(); // 生成 ID

        List<KnowledgePoint> children = node.children;
        for (KnowledgePoint child : children) {
            child.parentId = node.id;
            walk(child, index+1);
        }
    }

    private KnowledgePoint findNode(String nodeCode) {
        for (KnowledgePoint node : nodes) {
            if (node.code.equals(nodeCode)) {
                return node;
            }
        }

        return null;
    }

    private KnowledgePoint findParentNode(String nodeCode) {
        do {
            String parentCode = getParentCode(nodeCode);
            KnowledgePoint parent = findNode(parentCode);

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
}


