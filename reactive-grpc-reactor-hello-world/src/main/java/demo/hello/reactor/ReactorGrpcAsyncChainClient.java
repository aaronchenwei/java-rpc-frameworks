package demo.hello.reactor;

import demo.proto.HelloRequest;
import demo.proto.HelloResponse;
import demo.proto.ReactorGreeterGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * @author aaronchenwei
 */
public class ReactorGrpcAsyncChainClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) throws Exception {
    ManagedChannel channel = ManagedChannelBuilder
      .forAddress("localhost", 8888)
      .usePlaintext()
      .build();
    ReactorGreeterGrpc.ReactorGreeterStub stub = ReactorGreeterGrpc.newReactorStub(channel);

    Mono.just("World")
      // Call UNARY service asynchronously
      .map(ReactorGrpcAsyncChainClient::request)
      .as(stub::greet)
      .map(HelloResponse::getMessage)

      // Call STREAMING RESPONSE service asynchronously
      .map(ReactorGrpcAsyncChainClient::request)
      .as(stub::multiGreet)
      .map(HelloResponse::getMessage)

      // Call BI-DIRECTIONAL STREAMING service asynchronously
      .map(ReactorGrpcAsyncChainClient::request)
      .as(stub::streamGreet)
      .map(HelloResponse::getMessage)

      // Final processing
      .subscribe(
        LOGGER::info,
        Throwable::printStackTrace);

    Thread.sleep(Duration.ofSeconds(1).toMillis());

    // shutdown
    channel.shutdownNow().awaitTermination(30, TimeUnit.SECONDS);
  }

  private static HelloRequest request(String message) {
    return HelloRequest.newBuilder().setName(message).build();
  }
}
