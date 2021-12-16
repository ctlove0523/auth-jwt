package io.github.ctlove0523.grpc;

import io.github.ctlove0523.auth.jwt.core.Identity;
import io.github.ctlove0523.auth.jwt.core.TokenClient;
import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;

import java.util.concurrent.Executor;

public class AuthJwtCallCredentials extends CallCredentials {
    private final TokenClient tokenClient;
    private final Identity identity;

    public AuthJwtCallCredentials(TokenClient tokenClient, Identity identity) {
        this.tokenClient = tokenClient;
        this.identity = identity;
    }

    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
        String token = tokenClient.getToken(identity);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Metadata headers = new Metadata();
                    headers.put(AuthJwtGrpcConstants.AUTHORIZATION_METADATA_KEY,
                            String.format("%s %s", AuthJwtGrpcConstants.BEARER_TYPE, token));
                    metadataApplier.apply(headers);
                } catch (Throwable e) {
                    metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
                }
            }
        });
    }

    @Override
    public void thisUsesUnstableApi() {
        // no op
    }
}
