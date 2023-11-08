package cn.spider.framework.proto.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 **
 * 声明接口
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.42.1)",
    comments = "Source: transfer.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class TransferServerGrpc {

  private TransferServerGrpc() {}

  public static final String SERVICE_NAME = "cn.spider.framework.proto.grpc.TransferServer";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<cn.spider.framework.proto.grpc.TransferRequest,
      cn.spider.framework.proto.grpc.TransferResponse> getInstructMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "instruct",
      requestType = cn.spider.framework.proto.grpc.TransferRequest.class,
      responseType = cn.spider.framework.proto.grpc.TransferResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<cn.spider.framework.proto.grpc.TransferRequest,
      cn.spider.framework.proto.grpc.TransferResponse> getInstructMethod() {
    io.grpc.MethodDescriptor<cn.spider.framework.proto.grpc.TransferRequest, cn.spider.framework.proto.grpc.TransferResponse> getInstructMethod;
    if ((getInstructMethod = TransferServerGrpc.getInstructMethod) == null) {
      synchronized (TransferServerGrpc.class) {
        if ((getInstructMethod = TransferServerGrpc.getInstructMethod) == null) {
          TransferServerGrpc.getInstructMethod = getInstructMethod =
              io.grpc.MethodDescriptor.<cn.spider.framework.proto.grpc.TransferRequest, cn.spider.framework.proto.grpc.TransferResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "instruct"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  cn.spider.framework.proto.grpc.TransferRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  cn.spider.framework.proto.grpc.TransferResponse.getDefaultInstance()))
              .setSchemaDescriptor(new TransferServerMethodDescriptorSupplier("instruct"))
              .build();
        }
      }
    }
    return getInstructMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TransferServerStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TransferServerStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TransferServerStub>() {
        @java.lang.Override
        public TransferServerStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TransferServerStub(channel, callOptions);
        }
      };
    return TransferServerStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TransferServerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TransferServerBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TransferServerBlockingStub>() {
        @java.lang.Override
        public TransferServerBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TransferServerBlockingStub(channel, callOptions);
        }
      };
    return TransferServerBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TransferServerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TransferServerFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TransferServerFutureStub>() {
        @java.lang.Override
        public TransferServerFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TransferServerFutureStub(channel, callOptions);
        }
      };
    return TransferServerFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   **
   * 声明接口
   * </pre>
   */
  public static abstract class TransferServerImplBase implements io.grpc.BindableService {

    /**
     */
    public void instruct(cn.spider.framework.proto.grpc.TransferRequest request,
        io.grpc.stub.StreamObserver<cn.spider.framework.proto.grpc.TransferResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getInstructMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getInstructMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                cn.spider.framework.proto.grpc.TransferRequest,
                cn.spider.framework.proto.grpc.TransferResponse>(
                  this, METHODID_INSTRUCT)))
          .build();
    }
  }

  /**
   * <pre>
   **
   * 声明接口
   * </pre>
   */
  public static final class TransferServerStub extends io.grpc.stub.AbstractAsyncStub<TransferServerStub> {
    private TransferServerStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TransferServerStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TransferServerStub(channel, callOptions);
    }

    /**
     */
    public void instruct(cn.spider.framework.proto.grpc.TransferRequest request,
        io.grpc.stub.StreamObserver<cn.spider.framework.proto.grpc.TransferResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getInstructMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   **
   * 声明接口
   * </pre>
   */
  public static final class TransferServerBlockingStub extends io.grpc.stub.AbstractBlockingStub<TransferServerBlockingStub> {
    private TransferServerBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TransferServerBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TransferServerBlockingStub(channel, callOptions);
    }

    /**
     */
    public cn.spider.framework.proto.grpc.TransferResponse instruct(cn.spider.framework.proto.grpc.TransferRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getInstructMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   **
   * 声明接口
   * </pre>
   */
  public static final class TransferServerFutureStub extends io.grpc.stub.AbstractFutureStub<TransferServerFutureStub> {
    private TransferServerFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TransferServerFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TransferServerFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<cn.spider.framework.proto.grpc.TransferResponse> instruct(
        cn.spider.framework.proto.grpc.TransferRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getInstructMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_INSTRUCT = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TransferServerImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TransferServerImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_INSTRUCT:
          serviceImpl.instruct((cn.spider.framework.proto.grpc.TransferRequest) request,
              (io.grpc.stub.StreamObserver<cn.spider.framework.proto.grpc.TransferResponse>) responseObserver);
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

  private static abstract class TransferServerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TransferServerBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return cn.spider.framework.proto.grpc.SpiderTransferServer.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TransferServer");
    }
  }

  private static final class TransferServerFileDescriptorSupplier
      extends TransferServerBaseDescriptorSupplier {
    TransferServerFileDescriptorSupplier() {}
  }

  private static final class TransferServerMethodDescriptorSupplier
      extends TransferServerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TransferServerMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TransferServerGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TransferServerFileDescriptorSupplier())
              .addMethod(getInstructMethod())
              .build();
        }
      }
    }
    return result;
  }
}
