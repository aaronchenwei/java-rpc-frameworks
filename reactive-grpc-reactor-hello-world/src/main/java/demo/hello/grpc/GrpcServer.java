package demo.hello.grpc;

import demo.proto.GreeterGrpc;
import demo.proto.HelloRequest;
import demo.proto.HelloResponse;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This server implements a unary operation, a streaming response operation, and a bi-directional streaming operation.
 *
 * @author aaronchenwei
 */
public class GrpcServer extends GreeterGrpc.GreeterImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) throws Exception {
    // Start the server
    Server server = ServerBuilder
      .forPort(8888)
      .addService(new GrpcServer())
      .build()
      .start();
    LOGGER.atInfo().log("GrpcServer has been started.");

    server.awaitTermination();
  }

  /**
   * Implement a UNARY operation
   */
  @Override
  public void greet(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
    String name = request.getName();
    responseObserver.onNext(HelloResponse.newBuilder().setMessage("Hello " + name).build());
    responseObserver.onCompleted();
  }


  /**
   * Implement a STREAMING RESPONSE operation
   */
  @Override
  public void multiGreet(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
    String name = request.getName();
    responseObserver.onNext(HelloResponse.newBuilder().setMessage("Welcome " + name).build());
    responseObserver.onNext(HelloResponse.newBuilder().setMessage("Hola " + name).build());
    responseObserver.onNext(HelloResponse.newBuilder().setMessage("Bonjour " + name).build());
    responseObserver.onCompleted();
  }


  /**
   * Implement a BI-DIRECTIONAL STREAMING operation
   */
  @Override
  public StreamObserver<HelloRequest> streamGreet(StreamObserver<HelloResponse> responseObserver) {
    // Notice how the programming model completely changes
    return new StreamObserver<>() {
      @Override
      public void onNext(HelloRequest request) {
        String name = request.getName();
        responseObserver.onNext(HelloResponse.newBuilder().setMessage("Welcome " + name).build());
      }

      @Override
      public void onError(Throwable t) {
        responseObserver.onCompleted();
      }

      @Override
      public void onCompleted() {
        responseObserver.onCompleted();
      }
    };
  }
}
