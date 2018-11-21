import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * LMS 记录: xAPI数据规范 (人+时间+地点+设备+事件)
 */
@Getter
@Setter
@Accessors(chain = true)
public class LrsRecord {
    private String actionCode;     // 动作的编码: 如 CEPOP, SYULO
    private String actionDetails;  // 动作的详细信息

    private Long   actorId;        // 触发动作者的 ID
    private String actorUsername;  // 触发动作者的用户名

    private Long   targetId;       // 动作目标的 ID
    private String targetUsername; // 动作目标的用户名 (不是用户则忽略)
    private String targetDetails;  // 动作目标的详细信息: 如 用户、QA 题目、试卷题目等等

    private Date   createdTime;    // 创建时间
    private double longitude;      // 经度
    private double latitude;       // 纬度
    private String location;       // 地点
    private String device;         // 设备
    private Object event;          // 事件 (使用 JSON 组织数据)
}
