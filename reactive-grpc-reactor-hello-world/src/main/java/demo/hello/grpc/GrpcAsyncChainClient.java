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
public class GrpcAsyncChainClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) throws Exception {
    ManagedChannel channel = ManagedChannelBuilder
      .forAddress("localhost", 8888)
      .usePlaintext()
      .build();
    GreeterGrpc.GreeterStub stub = GreeterGrpc.newStub(channel);

    // Call UNARY service asynchronously
    stub.greet(HelloRequest.newBuilder().setName("World").build(), new StreamObserver<>() {

      @Override
      public void onNext(HelloResponse value) {
        // Handle BI-DIRECTIONAL STREAMING response
        StreamObserver<HelloRequest> streamGreetObserver = stub.streamGreet(new StreamObserver<>() {
          @Override
          public void onNext(HelloResponse value) {
            // Final processing
            LOGGER.atInfo()
              .setMessage("{}")
              .addArgument(value.getMessage())
              .log();
          }

          @Override
          public void onError(Throwable t) {
            LOGGER.atError().setCause(t).log();
          }

          @Override
          public void onCompleted() {
          }
        });

        // Call STREAMING RESPONSE service asynchronously
        stub.multiGreet(requestFromResponse(value), new StreamObserver<>() {

          @Override
          public void onNext(HelloResponse value) {
            // Call BI-DIRECTIONAL STREAMING service asynchronously
            streamGreetObserver.onNext(requestFromResponse(value));
          }

          @Override
          public void onError(Throwable t) {
            LOGGER.atError().setCause(t).log();
          }

          @Override
          public void onCompleted() {
            streamGreetObserver.onCompleted();
          }
        });
      }

      @Override
      public void onError(Throwable t) {
        LOGGER.atError().setCause(t).log();
      }

      @Override
      public void onCompleted() {
      }
    });

    Thread.sleep(Duration.ofSeconds(1).toMillis());

    // shutdown
    channel.shutdownNow().awaitTermination(30, TimeUnit.SECONDS);
  }

  private static HelloRequest requestFromResponse(HelloResponse response) {
    String message = response.getMessage();
    return HelloRequest.newBuilder().setName(message).build();
  }
}
