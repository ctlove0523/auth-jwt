package io.github.ctlove0523.auth.jwt.okhttp;

import io.github.ctlove0523.auth.jwt.core.Constants;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TestWebServer {
    private MockWebServer server;

    public TestWebServer() {
        this.server = new MockWebServer();
        this.server.setDispatcher(new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) throws InterruptedException {
                String token = recordedRequest.getHeader(Constants.TOKEN_KEY);
                MockResponse response = new MockResponse();
                response.setHeader(Constants.TOKEN_KEY, token);
                return response;
            }
        });

    }

    public int getPort() {
        return this.server.getPort();
    }

    public void start() {
        try {
            this.server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            this.server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
