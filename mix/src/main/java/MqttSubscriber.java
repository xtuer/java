import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.*;

import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Mqtt 的 subscriber, 订阅消息
 */
public class MqttSubscriber {
    private static final String TOPIC_NAME = "foo";
    private static final String WILL_TOPIC_NAME = "foo-will";
    private static final String HOST = "tcp://127.0.0.1:1883";

    public static void main(String[] args) throws URISyntaxException {
        final Promise<Buffer> result = new Promise<Buffer>();
        MQTT mqtt = new MQTT();
        mqtt.setHost(HOST);
        mqtt.setWillQos(QoS.AT_LEAST_ONCE);
        mqtt.setKeepAlive((short)3); // 默认是 30 秒
        mqtt.setReconnectDelay(500); // 500 毫秒重连一次, 默认是 10 毫秒
        mqtt.setReconnectDelayMax(500);

        // 订阅者持久化要同时满足下面 2 个条件, 消息发布者不需要
//        mqtt.setCleanSession(false);
        mqtt.setClientId("Biao1");

        final CallbackConnection connection = mqtt.callbackConnection();

        // 给 connection 添加事件监听器
        // 1. 当有消息到达时会调用 onPublish() 方法
        // 2. 连上服务器时调用 onConnected() 方法
        // 3. 和服务器的连接断开时调用 onDisconnected() 方法
        connection.listener(new Listener() {
            /**
             * 收到订阅的消息
             * @param topic 订阅队列的名字
             * @param payload 消息的内容
             * @param onComplete
             */
            @Override
            public void onPublish(UTF8Buffer topic, Buffer payload, Runnable onComplete) {
                result.onSuccess(payload);
                onComplete.run();

                // 消息处理逻辑
                System.out.println("Receive: " + new String(payload.toByteArray()));
            }

            /**
             * 成功连上服务器
             */
            @Override
            public void onConnected() {
                System.out.println("Connected to server");
            }

            /**
             * 从服务器断开连接
             */
            @Override
            public void onDisconnected() {
                System.out.println("Disconnected from server");
            }

            @Override
            public void onFailure(Throwable value) {
                System.out.println("Failure: " + value);
                result.onFailure(value);
                connection.disconnect(null);
            }
        });

        // 连接服务器
        connection.connect(new Callback<Void>() {
            /**
             * 第一次连接上服务器后订阅 topic. 没有放到 listener 的 onConnected() 方法,
             * 是因为重连后 onConnected() 会被再次调用, 订阅会被执行多次, 而 onSuccess()
             * 只有第一次连接上时会被执行, 这样订阅就只执行了一次.
             *
             * @param v Unknown parameter
             */
            @Override
            public void onSuccess(Void v) {
                // 可以同时订阅几个 topic, 订阅消息只需要保证送达一次即可
                Topic[] topics = {new Topic(TOPIC_NAME, QoS.AT_LEAST_ONCE), new Topic(WILL_TOPIC_NAME, QoS.AT_LEAST_ONCE)};

                connection.subscribe(topics, new Callback<byte[]>() {
                    @Override
                    public void onSuccess(byte[] value) {
                    }

                    @Override
                    public void onFailure(Throwable value) {
                        result.onFailure(value);
                        connection.disconnect(null);
                    }
                });
            }

            @Override
            public void onFailure(Throwable value) {
                result.onFailure(value);
            }
        });

        // 不让程序结束
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {}
        }, 1, 1, TimeUnit.SECONDS);
    }
}
