package io.github.ctlove0523.grpc;

import io.github.ctlove0523.auth.jwt.core.TokenCheckResult;
import io.github.ctlove0523.auth.jwt.core.TokenClient;
import io.grpc.*;

public class AuthJwtServerInterceptor implements ServerInterceptor {
    private TokenClient tokenClient;

    public AuthJwtServerInterceptor(TokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String value = metadata.get(AuthJwtGrpcConstants.AUTHORIZATION_METADATA_KEY);

        Status status = Status.OK;
        if (value == null) {
            status = Status.UNAUTHENTICATED.withDescription("Authorization token is missing");
        } else if (!value.startsWith(AuthJwtGrpcConstants.BEARER_TYPE)) {
            status = Status.UNAUTHENTICATED.withDescription("Unknown authorization type");
        } else {
            String token = value.substring(AuthJwtGrpcConstants.BEARER_TYPE.length()).trim();
            TokenCheckResult checkResult = tokenClient.validToken(token);
            if (!checkResult.pass()) {
                status = Status.UNAUTHENTICATED.withDescription("Token Invalid").withCause(checkResult.cause());
            }
            if (checkResult.getIdentity() != null) {
                Context ctx = Context.current()
                        .withValue(AuthJwtGrpcConstants.CLIENT_IDENTITY, checkResult.getIdentity());

                return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler);
            }
        }

        serverCall.close(status, new Metadata());
        return new ServerCall.Listener<ReqT>() {
            // noop
        };
    }
}
