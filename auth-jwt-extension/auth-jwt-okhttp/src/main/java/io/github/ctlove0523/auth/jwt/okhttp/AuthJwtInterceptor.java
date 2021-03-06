package io.github.ctlove0523.auth.jwt.okhttp;

import io.github.ctlove0523.auth.jwt.core.Constants;
import io.github.ctlove0523.auth.jwt.core.Identity;
import io.github.ctlove0523.auth.jwt.core.TokenClient;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class AuthJwtInterceptor implements Interceptor {
    private final TokenClient tokenClient;
    private final Identity identity;

    public AuthJwtInterceptor(TokenClient tokenClient, Identity identity) {
        Objects.requireNonNull(tokenClient, "tokenClient");
        Objects.requireNonNull(identity, "identity");
        this.tokenClient = tokenClient;
        this.identity = identity;
    }


    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();

        request = request.newBuilder().addHeader(Constants.TOKEN_KEY, tokenClient.getToken(identity))
                .build();

        return chain.proceed(request);
    }
}
