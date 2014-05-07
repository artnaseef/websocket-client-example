package com.artnaseef.websocketclient;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
* Created by art on 5/6/14.
*/
@WebSocket(maxTextMessageSize = 65536)
public class WebSocketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketHandler.class);
    private boolean connected = false;
    private Session session;

    @OnWebSocketConnect
    public void onConnect(Session sess) {
        synchronized ( this ) {
            this.connected = true;
            this.session   = sess;
            this.notifyAll();
        }

        LOG.info("Connected");
//        try {
//            sess.getRemote().sendString("John");
//            LOG.info("request sent");
//        } catch ( IOException ioExc ) {
//            ioExc.printStackTrace();
//        }
    }

    @OnWebSocketMessage
    public void onMessage(Session sess, String msg) {
        LOG.info("Server response: {}", msg);
    }

    @OnWebSocketClose
    public void onClose (int statusCode, String reason) {
        this.connected = false;
        LOG.info("Web socket closed; status={}, reason={}", statusCode, reason);
    }

    public void send (String msg) throws Exception {
        if ( ! this.connected ) {
            throw new Exception("the websocket connection is not open");
        }

        this.session.getRemote().sendString(msg);
    }

    public void waitUnitConnected () throws InterruptedException {
        synchronized ( this ) {
            while ( ! this.connected ) {
                this.wait();
            }
        }
    }
}
