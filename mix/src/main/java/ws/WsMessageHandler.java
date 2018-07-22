package ws;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@Accessors(chain = true)
public class WsMessageHandler implements IWsMsgHandler {
    private static Logger logger = LoggerFactory.getLogger(WsMessageHandler.class);
    private AtomicInteger userCount = new AtomicInteger(0);

    private Map<String, List<String>> userGroups; // 用户的小组
    private Map<String, List<String>> groupUsers; // 小组的用户

    /**
     * 握手时的回调函数，业务可以在这里获取 cookie，request 参数等
     */
    @Override
    public HttpResponse handshake(HttpRequest request, HttpResponse response, ChannelContext channelContext) throws Exception {
        // TODO: 校验用户的权限，无权则返回 null 拒绝握手
        // String token = request.getParam("token");

        return response;
    }

    /**
     * 连接关闭时的回调函数
     */
    @Override
    public Object onClose(WsRequest request, byte[] bytes, ChannelContext channelContext) throws Exception {
        int count = userCount.decrementAndGet();
        String content = channelContext.getClientNodeTraceFilename() + " 离开，还有 " + count + "人";
        WsMessage message = new WsMessage();
        message.setContent(content);
        logger.info(JSON.toJSONString(message));

        sendToGroup(message, "avatar", channelContext);
        Aio.remove(channelContext, "Receive close flag");

        return null;
    }

    /**
     * 收到文本消息时的回调函数
     */
    @Override
    public Object onText(WsRequest request, String text, ChannelContext channelContext) throws Exception {
        // 逻辑:
        // 1. 把接收到的消息转换为消息 WsMessage 对象，如果转换出错则消息格式不对，return 错误信息告知发送者
        // 2. 根据消息的类型分别进行处理
        //    2.1 加入小组的消息:
        //        2.1.1 小组名为空则加入失败
        //        2.1.2 加入小组
        //    2.2 发送小组消息
        //    2.3 发送私有消息
        //    2.4 不支持的消息
        try {
            WsMessage message = JSON.parseObject(text, WsMessage.class);
            String to = message.getTo();
            String groupName = to;

            switch (message.getType()) {
                case ADD_TO_GROUP:
                    // [2.1] 加入小组
                    if (StringUtils.isBlank(groupName)) {
                        return JSON.toJSONString(WsMessage.errorMessage("小组名不能为空"));
                    } else {
                        // TODO: 从 content 中得到用户的信息
                        Aio.bindGroup(channelContext, groupName); // 加入聊天组
                        sendToGroup(message, groupName, channelContext);
                        break;
                    }
                case GROUP_MESSAGE:
                    // [2.2] 发送小组消息
                    sendToGroup(message, groupName, channelContext);
                    break;
                case PRIVATE_MESSAGE:
                    // [2.3] 发送私有消息
                    // TODO: sendToUser
                    return JSON.toJSONString(WsMessage.unsupportedMessage());
                default:
                    // [2.4] 不支持的消息
                    return JSON.toJSONString(WsMessage.unsupportedMessage());
            }

            return null;
        } catch (Exception ex) {
            // 转换出错则消息格式不对
            return JSON.toJSONString(WsMessage.unsupportedMessage());
        }
    }

    public void sendToGroup(WsMessage message, String groupName, ChannelContext channelContext) {
        System.out.println(JSON.toJSONString(message));
        WsResponse response = WsResponse.fromText(JSON.toJSONString(message), "UTF-8");
        Aio.sendToGroup(channelContext.getGroupContext(), groupName, response);
    }

    @Override
    public void onAfterHandshaked(HttpRequest request, HttpResponse response, ChannelContext channelContext) throws Exception {
        int count = userCount.incrementAndGet();
        String message = channelContext + " 加入，共有 " + count + " 人";
        logger.info(message);
    }

    @Override
    public Object onBytes(WsRequest request, byte[] bytes, ChannelContext channelContext) throws Exception {
        return null;
    }
}
