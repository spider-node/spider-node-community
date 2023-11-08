package cn.spider.framework.proto.grpc;

import static cn.spider.framework.proto.grpc.TransferServerGrpc.getServiceDescriptor;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;


@javax.annotation.Generated(
value = "by VertxGrpc generator",
comments = "Source: transfer.proto")
public final class VertxTransferServerGrpc {
    private VertxTransferServerGrpc() {}

    public static TransferServerVertxStub newVertxStub(io.grpc.Channel channel) {
        return new TransferServerVertxStub(channel);
    }

    /**
     * <pre>
     * &#42;
     *  声明接口
     * </pre>
     */
    public static final class TransferServerVertxStub extends io.grpc.stub.AbstractStub<TransferServerVertxStub> {
        private final io.vertx.core.impl.ContextInternal ctx;
        private TransferServerGrpc.TransferServerStub delegateStub;

        private TransferServerVertxStub(io.grpc.Channel channel) {
            super(channel);
            delegateStub = TransferServerGrpc.newStub(channel);
            this.ctx = (io.vertx.core.impl.ContextInternal) io.vertx.core.Vertx.currentContext();
        }

        private TransferServerVertxStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
            delegateStub = TransferServerGrpc.newStub(channel).build(channel, callOptions);
            this.ctx = (io.vertx.core.impl.ContextInternal) io.vertx.core.Vertx.currentContext();
        }

        @Override
        protected TransferServerVertxStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new TransferServerVertxStub(channel, callOptions);
        }

        
        public io.vertx.core.Future<cn.spider.framework.proto.grpc.TransferResponse> instruct(cn.spider.framework.proto.grpc.TransferRequest request) {
            return io.vertx.grpc.stub.ClientCalls.oneToOne(ctx, request, delegateStub::instruct);
        }

    }

    /**
     * <pre>
     * &#42;
     *  声明接口
     * </pre>
     */
    public static abstract class TransferServerVertxImplBase implements io.grpc.BindableService {
        private String compression;

        /**
         * Set whether the server will try to use a compressed response.
         *
         * @param compression the compression, e.g {@code gzip}
         */
        public TransferServerVertxImplBase withCompression(String compression) {
            this.compression = compression;
            return this;
        }

        
        public io.vertx.core.Future<cn.spider.framework.proto.grpc.TransferResponse> instruct(cn.spider.framework.proto.grpc.TransferRequest request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
                    .addMethod(
                            cn.spider.framework.proto.grpc.TransferServerGrpc.getInstructMethod(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            cn.spider.framework.proto.grpc.TransferRequest,
                                            cn.spider.framework.proto.grpc.TransferResponse>(
                                            this, METHODID_INSTRUCT, compression)))
                    .build();
        }
    }

    private static final int METHODID_INSTRUCT = 0;

    private static final class MethodHandlers<Req, Resp> implements
            io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {

        private final TransferServerVertxImplBase serviceImpl;
        private final int methodId;
        private final String compression;

        MethodHandlers(TransferServerVertxImplBase serviceImpl, int methodId, String compression) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
            this.compression = compression;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                case METHODID_INSTRUCT:
                    io.vertx.grpc.stub.ServerCalls.oneToOne(
                            (cn.spider.framework.proto.grpc.TransferRequest) request,
                            (io.grpc.stub.StreamObserver<cn.spider.framework.proto.grpc.TransferResponse>) responseObserver,
                            compression,
                            serviceImpl::instruct);
                    break;
                default:
                    throw new java.lang.AssertionError();
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                default:
                    throw new java.lang.AssertionError();
            }
        }
    }

}
