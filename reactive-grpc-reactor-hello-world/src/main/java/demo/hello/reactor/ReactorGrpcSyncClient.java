package demo.hello.reactor;

import demo.proto.HelloRequest;
import demo.proto.HelloResponse;
import demo.proto.ReactorGreeterGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author aaronchenwei
 */
public class ReactorGrpcSyncClient {

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
    System.out.println(request
      // Call service
      .as(stub::greet)
      // Map response
      .map(HelloResponse::getMessage)
      .block());

    /*
     * Call an async STREAMING RESPONSE operation
     */
    request
      // Call service
      .as(stub::multiGreet)
      // Map response
      .map(HelloResponse::getMessage)
      .doOnNext(System.out::println)
      .blockLast();

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
      .doOnNext(System.out::println)
      .blockLast();
  }
}
