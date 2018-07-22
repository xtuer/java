package ws;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class WsMessage {
    private String from;    // 消息发送者
    private String to;      // 消息接收者
    private String content; // 消息内容
    private Type   type;    // 消息类型

    // 消息类型
    public enum Type {
        ADD_TO_GROUP,    // 加入小组
        GROUP_MESSAGE,   // 发送小组消息
        GROUP_USERS,     // 获取小组成员信息
        PRIVATE_MESSAGE, // 发送私有消息
        ERROR
    }

    public static WsMessage unsupportedMessage() {
        WsMessage msg = new WsMessage();
        msg.setContent("不支持的消息");
        msg.setType(WsMessage.Type.ERROR);

        return msg;
    }

    public static WsMessage errorMessage(String content) {
        WsMessage msg = new WsMessage();
        msg.setContent(content);
        msg.setType(WsMessage.Type.ERROR);

        return msg;
    }
}
