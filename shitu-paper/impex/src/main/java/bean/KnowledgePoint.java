package bean;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@JSONType(ignores = {"children"})
public class KnowledgePoint {
    Long   id = 0L;
    Long   parentId = 0L;
    String code; // 知识点编码
    String name; // 知识点名称
    String subjectName; // 科目名称，例如物理
    String subjectCode; // 科目编码，例如 GZWL061B

    List<KnowledgePoint> children = new LinkedList<>();

    public KnowledgePoint() {

    }

    public KnowledgePoint(String code, String name, String subjectName, String subjectCode) {
        this.code = code;
        this.name = name;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }
}
