import org.fusesource.mqtt.client.*;

import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MqttPublisher {
    private static final String TOPIC_NAME = "foo";
    private static final String WILL_TOPIC_NAME = "foo-will";
    private static final String HOST = "tcp://ebag.qderzhong.net:1883";
//    private static final String HOST = "tcp://127.0.0.1:1883";
    private static int messageNumber = 0;

    public static void main(String[] args) throws URISyntaxException {
        MQTT mqtt = new MQTT();
        mqtt.setHost(HOST);
        mqtt.setKeepAlive((short)10); // 默认是 30 秒
        mqtt.setReconnectDelay(500); // 500 毫秒重连一次, 默认是 10 毫秒
        mqtt.setReconnectDelayMax(500);
        mqtt.setWillTopic(WILL_TOPIC_NAME);
        mqtt.setWillQos(QoS.EXACTLY_ONCE);
        mqtt.setWillMessage("Will message: My ID is A, I have left. 3"); // 多个客户端的 will message 都可以放到同一个 topic 里

        final FutureConnection connection = mqtt.futureConnection();

        connection.connect().then(new Callback<Void>(){
            public void onSuccess(Void value) {
                System.out.println("Connected");
            }
            public void onFailure(Throwable e) {
                System.out.println("Failed");
            }
        });

        // 不停的发消息
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (connection != null && connection.isConnected()) {
                    String message = (++messageNumber) + " - A - 时间: " + System.currentTimeMillis();
                    Future<Void> future = connection.publish(TOPIC_NAME, message.getBytes(), QoS.EXACTLY_ONCE, false);

                    try {
                        future.await(2, TimeUnit.SECONDS); // 等待消息发送完成, 如果发送失败就会抛出异常, 例如网络断开的情况下
                        System.out.println("Send: " + message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Non-connected");
                }
            }
        }, 1000, 100, TimeUnit.MILLISECONDS);
    }
}
