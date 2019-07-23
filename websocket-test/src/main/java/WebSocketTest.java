import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// https://github.com/TooTallNate/Java-WebSocket
public class WebSocketTest {
    public static void main(String[] args) throws URISyntaxException {
        // java -jar -Dhost=localhost -Dport=3721 WebSocketTest.jar
        String host = System.getProperty("host", "localhost");
        String port = System.getProperty("port", "3721");

        WebSocketClient mWs = new WebSocketClient(new URI("ws://" + host + ":" + port + "?userId=1&username=biao")) {
            @Override
            public void onMessage(String message) {
                System.out.println(message);
            }

            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("opened connection");
                WebSocketClient self = this;

                // 每 5 秒发送一次消息
                Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                    self.send("{\"type\": \"Noop\"}");
                }, 0, 5, TimeUnit.SECONDS);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("closed connection");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        mWs.connect();
    }
}
