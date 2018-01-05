package bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PaperDirectory {
    private String paperDirectoryId;
    private String parentPaperDirectoryId;
    private String name;
    private String relativePath; // 用于加载试卷
}
