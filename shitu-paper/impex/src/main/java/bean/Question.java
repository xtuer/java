package bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Question {
    private Long id;  // 题目的 ID
    private int type; // 题目的类型
    private String originalId;  // 题目在乐教乐学的数据库中的 ID
    private String content;     // 题干
    private String analysis;    // 解析
    private String subjectCode; // 科目编码
    private String knowledgePointCode; // 知识点的编码
    private Long   knowledgePointId;   // 知识点的 ID
}
