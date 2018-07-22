package ws;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tio.websocket.server.WsServerStarter;

import java.io.IOException;

@Getter
@Setter
@Accessors(chain = true)
public class ImServer {
    public static void main(String[] args) throws IOException {
        WsServerStarter serverStarter = new WsServerStarter(3721, new WsMessageHandler());
        serverStarter.start();
    }
}
