package io.github.ctlove0523.auth.jwt.samples.grpc;

import io.github.ctlove0523.grpc.AuthJwtCallCredentials;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static io.github.ctlove0523.auth.jwt.samples.grpc.AuthJwtGrpcUtils.getIdentity;
import static io.github.ctlove0523.auth.jwt.samples.grpc.AuthJwtGrpcUtils.getTokenClient;

public class AuthJwtGrpcServerClient {
    public static void main(String[] args) {
        CallCredentials callCredentials = new AuthJwtCallCredentials(getTokenClient(), getIdentity());
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5332)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();
        AuthJwtGrpc.AuthJwtBlockingStub stub = AuthJwtGrpc.newBlockingStub(channel);

        HelloRequest request = HelloRequest.newBuilder()
                .setName("auth jwt")
                .build();

        HelloReply reply = stub.withCallCredentials(callCredentials)
                .sayHello(request);
        System.out.println(reply);
    }
}
