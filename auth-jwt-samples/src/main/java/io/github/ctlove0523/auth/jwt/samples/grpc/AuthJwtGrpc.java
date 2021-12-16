package io.github.ctlove0523.auth.jwt.samples.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 *
 */
@javax.annotation.Generated(
        value = "by gRPC proto compiler (version 1.4.0)",
        comments = "Source: auth_jwt.proto")
public final class AuthJwtGrpc {

    private AuthJwtGrpc() {
    }

    public static final String SERVICE_NAME = "grpc.AuthJwt";

    // Static method descriptors that strictly reflect the proto.
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
    public static final io.grpc.MethodDescriptor<io.github.ctlove0523.auth.jwt.samples.grpc.HelloRequest,
            io.github.ctlove0523.auth.jwt.samples.grpc.HelloReply> METHOD_SAY_HELLO =
            io.grpc.MethodDescriptor.<io.github.ctlove0523.auth.jwt.samples.grpc.HelloRequest, io.github.ctlove0523.auth.jwt.samples.grpc.HelloReply>newBuilder()
                    .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                    .setFullMethodName(generateFullMethodName(
                            "grpc.AuthJwt", "SayHello"))
                    .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                            io.github.ctlove0523.auth.jwt.samples.grpc.HelloRequest.getDefaultInstance()))
                    .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                            io.github.ctlove0523.auth.jwt.samples.grpc.HelloReply.getDefaultInstance()))
                    .build();

    /**
     * Creates a new async stub that supports all call types for the service
     */
    public static AuthJwtStub newStub(io.grpc.Channel channel) {
        return new AuthJwtStub(channel);
    }

    /**
     * Creates a new blocking-style stub that supports unary and streaming output calls on the service
     */
    public static AuthJwtBlockingStub newBlockingStub(
            io.grpc.Channel channel) {
        return new AuthJwtBlockingStub(channel);
    }

    /**
     * Creates a new ListenableFuture-style stub that supports unary calls on the service
     */
    public static AuthJwtFutureStub newFutureStub(
            io.grpc.Channel channel) {
        return new AuthJwtFutureStub(channel);
    }

    /**
     *
     */
    public static abstract class AuthJwtImplBase implements io.grpc.BindableService {

        /**
         *
         */
        public void sayHello(io.github.ctlove0523.auth.jwt.samples.grpc.HelloRequest request,
                             io.grpc.stub.StreamObserver<io.github.ctlove0523.auth.jwt.samples.grpc.HelloReply> responseObserver) {
            asyncUnimplementedUnaryCall(METHOD_SAY_HELLO, responseObserver);
        }

        @java.lang.Override
        public final io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
                    .addMethod(
                            METHOD_SAY_HELLO,
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            io.github.ctlove0523.auth.jwt.samples.grpc.HelloRequest,
                                            io.github.ctlove0523.auth.jwt.samples.grpc.HelloReply>(
                                            this, METHODID_SAY_HELLO)))
                    .build();
        }
    }

    /**
     *
     */
    public static final class AuthJwtStub extends io.grpc.stub.AbstractStub<AuthJwtStub> {
        private AuthJwtStub(io.grpc.Channel channel) {
            super(channel);
        }

        private AuthJwtStub(io.grpc.Channel channel,
                            io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected AuthJwtStub build(io.grpc.Channel channel,
                                    io.grpc.CallOptions callOptions) {
            return new AuthJwtStub(channel, callOptions);
        }

        /**
         *
         */
        public void sayHello(io.github.ctlove0523.auth.jwt.samples.grpc.HelloRequest request,
                             io.grpc.stub.StreamObserver<io.github.ctlove0523.auth.jwt.samples.grpc.HelloReply> responseObserver) {
            asyncUnaryCall(
                    getChannel().newCall(METHOD_SAY_HELLO, getCallOptions()), request, responseObserver);
        }
    }

    /**
     *
     */
    public static final class AuthJwtBlockingStub extends io.grpc.stub.AbstractStub<AuthJwtBlockingStub> {
        private AuthJwtBlockingStub(io.grpc.Channel channel) {
            super(channel);
        }

        private AuthJwtBlockingStub(io.grpc.Channel channel,
                                    io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected AuthJwtBlockingStub build(io.grpc.Channel channel,
                                            io.grpc.CallOptions callOptions) {
            return new AuthJwtBlockingStub(channel, callOptions);
        }

        /**
         *
         */
        public io.github.ctlove0523.auth.jwt.samples.grpc.HelloReply sayHello(io.github.ctlove0523.auth.jwt.samples.grpc.HelloRequest request) {
            return blockingUnaryCall(
                    getChannel(), METHOD_SAY_HELLO, getCallOptions(), request);
        }
    }

    /**
     *
     */
    public static final class AuthJwtFutureStub extends io.grpc.stub.AbstractStub<AuthJwtFutureStub> {
        private AuthJwtFutureStub(io.grpc.Channel channel) {
            super(channel);
        }

        private AuthJwtFutureStub(io.grpc.Channel channel,
                                  io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected AuthJwtFutureStub build(io.grpc.Channel channel,
                                          io.grpc.CallOptions callOptions) {
            return new AuthJwtFutureStub(channel, callOptions);
        }

        /**
         *
         */
        public com.google.common.util.concurrent.ListenableFuture<io.github.ctlove0523.auth.jwt.samples.grpc.HelloReply> sayHello(
                io.github.ctlove0523.auth.jwt.samples.grpc.HelloRequest request) {
            return futureUnaryCall(
                    getChannel().newCall(METHOD_SAY_HELLO, getCallOptions()), request);
        }
    }

    private static final int METHODID_SAY_HELLO = 0;

    private static final class MethodHandlers<Req, Resp> implements
            io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
        private final AuthJwtImplBase serviceImpl;
        private final int methodId;

        MethodHandlers(AuthJwtImplBase serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                case METHODID_SAY_HELLO:
                    serviceImpl.sayHello((io.github.ctlove0523.auth.jwt.samples.grpc.HelloRequest) request,
                            (io.grpc.stub.StreamObserver<io.github.ctlove0523.auth.jwt.samples.grpc.HelloReply>) responseObserver);
                    break;
                default:
                    throw new AssertionError();
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(
                io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                default:
                    throw new AssertionError();
            }
        }
    }

    private static final class AuthJwtDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
        @java.lang.Override
        public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
            return io.github.ctlove0523.auth.jwt.samples.grpc.AuthJwtGrpcService.getDescriptor();
        }
    }

    private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

    public static io.grpc.ServiceDescriptor getServiceDescriptor() {
        io.grpc.ServiceDescriptor result = serviceDescriptor;
        if (result == null) {
            synchronized (AuthJwtGrpc.class) {
                result = serviceDescriptor;
                if (result == null) {
                    serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                            .setSchemaDescriptor(new AuthJwtDescriptorSupplier())
                            .addMethod(METHOD_SAY_HELLO)
                            .build();
                }
            }
        }
        return result;
    }
}
