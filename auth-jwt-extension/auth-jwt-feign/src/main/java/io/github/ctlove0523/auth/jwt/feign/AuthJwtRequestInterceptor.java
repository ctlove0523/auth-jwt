package io.github.ctlove0523.auth.jwt.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.github.ctlove0523.auth.jwt.core.Constants;
import io.github.ctlove0523.auth.jwt.core.Identity;
import io.github.ctlove0523.auth.jwt.core.TokenClient;

import java.util.Objects;

public class AuthJwtRequestInterceptor implements RequestInterceptor {
    private final TokenClient tokenClient;
    private final Identity identity;

    public AuthJwtRequestInterceptor(TokenClient tokenClient, Identity identity) {
        Objects.requireNonNull(tokenClient, "tokenClient");
        Objects.requireNonNull(identity, "identity");
        this.tokenClient = tokenClient;
        this.identity = identity;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(Constants.TOKEN_KEY, tokenClient.getToken(identity));
    }
}
