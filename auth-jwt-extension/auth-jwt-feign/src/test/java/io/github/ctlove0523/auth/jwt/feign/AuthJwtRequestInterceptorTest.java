package io.github.ctlove0523.auth.jwt.feign;

import feign.RequestTemplate;
import io.github.ctlove0523.auth.jwt.core.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

public class AuthJwtRequestInterceptorTest {

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

    private final AuthJwtRequestInterceptor interceptor = new AuthJwtRequestInterceptor(tokenClient, identity);


    @Test
    public void testApply_shouRequestContainToken_when_tokenClientWork() throws IOException {
        RequestTemplate requestTemplate = new RequestTemplate();

        interceptor.apply(requestTemplate);

        Collection<String> token = requestTemplate.headers().get(Constants.TOKEN_KEY);
        Assert.assertTrue(token.contains(DEFAULT_TOKEN));
    }

}
