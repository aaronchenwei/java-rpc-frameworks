package demo.hello.reactor;

import demo.proto.HelloRequest;
import demo.proto.HelloResponse;
import demo.proto.ReactorGreeterGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.time.Duration;
import reactor.core.publisher.Mono;

/**
 * @author aaronchenwei
 */
public class ReactorGrpcAsyncChainClient {

  public static void main(String[] args) throws Exception {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8888).usePlaintext().build();
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
        System.out::println,
        Throwable::printStackTrace);

    Thread.sleep(Duration.ofSeconds(1).toMillis());
  }

  private static HelloRequest request(String message) {
    return HelloRequest.newBuilder().setName(message).build();
  }
}
