package com.artnaseef.websocketclient;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimpleWebsocketClient {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleWebsocketClient.class);

    private WebSocketClient         client;
    private WebSocketHandler        handler;
    private String                  websocketUrl = "ws://localhost:8080/ws/agent";
    private ClientUpgradeRequest    upgradeRequest = new ClientUpgradeRequest();

    public static void  main (String[] args) {
        new SimpleWebsocketClient().run(args);
    }

    public SimpleWebsocketClient () {
        this.handler = new WebSocketHandler();
    }

    public void run (String[] args) {
        try {
            connect();
            LOG.info("starting web socket");
            startWebsocket(this.websocketUrl);

            LOG.info("web socket connection initiated; waiting for connection");
            this.handler.waitUnitConnected();

            LOG.info("web socket connected");
            for ( String oneName : args ) {
                this.handler.send(oneName);
            }

            readUserInput();
        } catch ( Exception exc ) {
            LOG.error("Websocket test client failed", exc);
        }
    }

    protected void  connect () throws Exception {
        this.client = new WebSocketClient();
        this.client.start();
    }

    protected void  startWebsocket (String url) throws URISyntaxException, IOException {
        this.client.connect(this.handler, new URI(url), upgradeRequest);
    }

    protected void  readUserInput () throws Exception {
        BufferedReader  rdr;

        rdr = new BufferedReader(new InputStreamReader(System.in));
        this.promptUser();
        String line = rdr.readLine();

        while ( line != null ) {
            if ( ! line.isEmpty() ) {
                LOG.info("Sending {}", line);
                this.handler.send(line);
            }
            this.promptUser();
            line = rdr.readLine();
        }
    }

    protected void  promptUser () {
        System.out.print("Name: ");
        System.out.flush();
    }
}