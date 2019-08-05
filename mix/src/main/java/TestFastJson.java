import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unchecked")
public class TestFastJson {
    public static void main(String[] args) {
        Box box = new Box("Foo");

        String json = JSON.toJSONString(box);
        System.out.println(json); // {"data":"Foo"}

        box = JSON.parseObject(json, Box.class);
        System.out.println(box); // Box{data=Foo}

        List<Box> list = new LinkedList<>();
        list.add(new Box("Alice"));
        list.add(new Box("John"));
        json = JSON.toJSONString(list);
        System.out.println(json); // [{"data":"Alice"},{"data":"John"}]

        list = JSON.parseObject(json, List.class);
        System.out.println(list); // [{"data":"Alice"}, {"data":"John"}]，可以看到，输出的不是 Box.toString() 输出的内容，说明 list 中存储的不是 Box
        // System.out.println(list.get(0).getClass()); // Error: ClassCastException: com.alibaba.fastjson.JSONObject cannot be cast to Box

        list = JSON.parseObject(json, new TypeReference<List<Box>>() {}); // 使用 parseObject() 转换的结果使用范型时使用 TypeReference
        System.out.println(list.get(0).getClass()); // class Box
        System.out.println(list); // [Box{data=Alice}, Box{data=John}]
    }
}

class Box {
    private String data;

    public Box() {

    }

    public Box(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("Box{data=%s}", data);
    }
}
