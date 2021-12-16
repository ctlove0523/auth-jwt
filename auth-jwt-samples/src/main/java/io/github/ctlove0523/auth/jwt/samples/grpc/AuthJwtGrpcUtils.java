package io.github.ctlove0523.auth.jwt.samples.grpc;

import io.github.ctlove0523.auth.jwt.core.Identity;
import io.github.ctlove0523.auth.jwt.core.TokenCheckPassResult;
import io.github.ctlove0523.auth.jwt.core.TokenCheckResult;
import io.github.ctlove0523.auth.jwt.core.TokenClient;

import java.util.Date;

public class AuthJwtGrpcUtils {
    private static final String DEFAULT_TOKEN = "grpc token";

    public static TokenClient getTokenClient() {
        return new TokenClient() {
            @Override
            public String getToken(Identity identity) {
                return DEFAULT_TOKEN;
            }

            @Override
            public TokenCheckResult validToken(String token) {
                Identity identity = Identity.newIdentity().setId("id");
                return new TokenCheckPassResult(new Date(), identity);
            }
        };
    }

    public static Identity getIdentity() {
        return Identity.newIdentity().setId("id");
    }

}
