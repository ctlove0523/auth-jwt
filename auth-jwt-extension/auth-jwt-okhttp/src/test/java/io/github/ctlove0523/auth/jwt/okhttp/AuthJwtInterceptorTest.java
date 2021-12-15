package io.github.ctlove0523.auth.jwt.okhttp;

import io.github.ctlove0523.auth.jwt.core.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class AuthJwtInterceptorTest {
    private static final String DEFAULT_TOKEN = "token for test";

    private final TokenClient tokenClient = new TokenClient() {
        @Override
        public String getToken(Identity identity) {
            return DEFAULT_TOKEN;
        }

        @Override
        public TokenCheckResult validToken(String token) {
            return new TokenCheckPassResult(new Date());
        }

    };

    private final Identity identity = Identity.newIdentity();

    private final AuthJwtInterceptor interceptor = new AuthJwtInterceptor(tokenClient, identity);
    private final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build();

    private final TestWebServer testWebServer = new TestWebServer();

    @Before
    public void startServer() {
        testWebServer.start();
    }

    @After
    public void stopServer() {
        testWebServer.stop();
    }

    @Test
    public void testIntercept_shouRequestContainToken_when_tokenClientWork() throws IOException {
        int port = testWebServer.getPort();
        Request request = new Request.Builder()
                .get()
                .url("http://localhost:"+port)
                .build();
            Response response = client.newCall(request).execute();
            String token = response.header("X-Auth-Jwt-Token");

        Assert.assertEquals(token, DEFAULT_TOKEN);
    }

}
