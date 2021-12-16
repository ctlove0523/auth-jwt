package io.github.ctlove0523.auth.jwt.samples.grpc;

import io.github.ctlove0523.grpc.AuthJwtServerInterceptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import static io.github.ctlove0523.auth.jwt.samples.grpc.AuthJwtGrpcUtils.getTokenClient;

public class AuthJwtGrpcServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(5332)
                .addService(new AuthJwtGrpcImpl())
                .intercept(new AuthJwtServerInterceptor(getTokenClient()))
                .build()
                .start();

        server.awaitTermination();
    }

    private static final class AuthJwtGrpcImpl extends AuthJwtGrpc.AuthJwtImplBase {
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder()
                    .setMessage("hello " + request.getName())
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
