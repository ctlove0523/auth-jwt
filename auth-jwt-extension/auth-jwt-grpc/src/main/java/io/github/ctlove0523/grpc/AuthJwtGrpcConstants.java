package io.github.ctlove0523.grpc;

import io.github.ctlove0523.auth.jwt.core.Identity;
import io.grpc.Context;
import io.grpc.Metadata;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

class AuthJwtGrpcConstants {
    public static final String BEARER_TYPE = "Bearer";
    static final Metadata.Key<String> AUTHORIZATION_METADATA_KEY = Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER);
    static final Context.Key<Identity> CLIENT_IDENTITY = Context.key("ClientIdentity");
}
