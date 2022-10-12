package demo.hello.grpc;

import demo.proto.GreeterGrpc;
import demo.proto.HelloRequest;
import demo.proto.HelloResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aaronchenwei
 */
public class GrpcAsyncClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) throws Exception {
    ManagedChannel channel = ManagedChannelBuilder
      .forAddress("localhost", 8888)
      .usePlaintext()
      .build();
    GreeterGrpc.GreeterStub stub = GreeterGrpc.newStub(channel);

    HelloRequest request = HelloRequest.newBuilder().setName("World").build();

    /*
     * Create a request callback observer.
     */
    StreamObserver<HelloResponse> responseObserver = new StreamObserver<>() {
      @Override
      public void onNext(HelloResponse response) {
        LOGGER.atInfo()
          .setMessage("{}")
          .addArgument(response.getMessage())
          .log();
      }

      @Override
      public void onError(Throwable t) {
        LOGGER.atError()
          .setMessage("{}")
          .addArgument(t.getMessage())
          .setCause(t)
          .log();
      }

      @Override
      public void onCompleted() {
        LOGGER.atInfo()
          .setMessage("onCompleted()")
          .log();
      }
    };

    /*
     * Call an async UNARY operation
     * Callbacks defined outside of service invocation :(
     */
    stub.greet(request, responseObserver);

    /*
     * Call an async STREAMING RESPONSE operation
     * Callbacks defined outside of service invocation :(
     */
    stub.multiGreet(request, responseObserver);

    /*
     * Call an async BI-DIRECTIONAL STREAMING operation
     * Callbacks defined outside of service invocation :(
     */
    StreamObserver<HelloRequest> requestObserver = stub.streamGreet(responseObserver);
    // Notice how the programming model completely changes
    requestObserver.onNext(HelloRequest.newBuilder().setName("Alpha").build());
    requestObserver.onNext(HelloRequest.newBuilder().setName("Beta").build());
    requestObserver.onNext(HelloRequest.newBuilder().setName("Gamma").build());
    requestObserver.onCompleted();

    Thread.sleep(Duration.ofSeconds(1).toMillis());

    // shutdown
    channel.shutdownNow().awaitTermination(30, TimeUnit.SECONDS);
  }
}
