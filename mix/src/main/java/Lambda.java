import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(chain = true)
public class Lambda {
    private int id;
    private String name;

    public Lambda(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<Lambda> prepareData() {
        List<Lambda> lms = new LinkedList<>();
        lms.add(new Lambda(1, "Alice"));
        lms.add(new Lambda(1, "Bob"));
        lms.add(new Lambda(1, "John"));
        lms.add(new Lambda(2, "Bob"));
        lms.add(new Lambda(2, "Steven"));
        lms.add(new Lambda(3, "John"));
        lms.add(new Lambda(3, "Loa"));

        return lms;
    }

    /**
     * 介绍 Lambda 的几个常用方法，更多的请参考 JDK 文档 Collectors
     * 只有 sort 会改变原来集合的数据，其他操作不会
     */
    public static void main(String[] args) {
        List<Lambda> lms = prepareData();

        // 1. 过滤 id 小于 3 的元素
        lms.stream().filter(e -> e.getId() > 2).forEach(e -> {
            System.out.println(JSON.toJSONString(e));
        });

        // 2. 获取所有不同的名字，返回 Set
        Set<String> names = lms.stream().map(Lambda::getName).collect(Collectors.toCollection(TreeSet::new));
        System.out.println(names);

        // 3. 合并为字符串
        System.out.println(names.stream().collect(Collectors.joining(", ")));
        System.out.println(String.join(", ", names));

        // 4. 排序: 先按名字升序排序，名字相同按 id 降序排序
        lms.sort(Comparator.comparing(Lambda::getName).thenComparing(Lambda::getId).reversed());
        System.out.println(JSON.toJSONString(lms));

        // 5. 使用 Map 分组，id 相同的放在一组
        // 6. 遍历 Map
        Map<Integer, List<Lambda>> groupedLms = lms.stream().collect(Collectors.groupingBy(Lambda::getId));
        groupedLms.forEach((id, lms2) -> {
            System.out.println(id + ": " + JSON.toJSONString(lms2));
        });
    }
}
