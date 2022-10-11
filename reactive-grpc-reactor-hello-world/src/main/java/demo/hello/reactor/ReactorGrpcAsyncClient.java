package demo.hello.reactor;

import demo.proto.HelloRequest;
import demo.proto.HelloResponse;
import demo.proto.ReactorGreeterGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.time.Duration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author aaronchenwei
 */
public class ReactorGrpcAsyncClient {

  public static void main(String[] args) throws Exception {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8888).usePlaintext().build();
    ReactorGreeterGrpc.ReactorGreeterStub stub = ReactorGreeterGrpc.newReactorStub(channel);

    /*
     * Create a service request
     */
    Mono<HelloRequest> request = Mono.just(HelloRequest.newBuilder().setName("World").build());

    /*
     * Call an async UNARY operation
     */
    request
      // Call service
      .as(stub::greet)
      // Map response
      .map(HelloResponse::getMessage)
      .subscribe(System.out::println);

    /*
     * Call an async STREAMING RESPONSE operation
     */
    request
      // Call service
      .as(stub::multiGreet)
      // Map response
      .map(HelloResponse::getMessage)
      .subscribe(System.out::println);

    /*
     * Call an async BI-DIRECTIONAL STREAMING operation
     */
    Flux
      // Call service
      .just("Alpha", "Beta", "Gamma")
      .map(name -> HelloRequest.newBuilder().setName(name).build())
      .as(stub::streamGreet)
      // Map response
      .map(HelloResponse::getMessage)
      .subscribe(System.out::println);

    Thread.sleep(Duration.ofSeconds(1).toMillis());
  }
}
