package demo.hello.reactor;

import demo.proto.HelloRequest;
import demo.proto.HelloResponse;
import demo.proto.ReactorGreeterGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

/**
 * This server implements a unary operation, a streaming response operation, and a bi-directional streaming operation.
 *
 * @author aaronchenwei
 */
public class ReactorGrpcServer extends ReactorGreeterGrpc.GreeterImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) throws Exception {
    // Start the server
    Server server = ServerBuilder
      .forPort(8888)
      .addService(new ReactorGrpcServer())
      .build()
      .start();

    LOGGER.atInfo().log("Reactor GRPC Server has been started.");

    server.awaitTermination();
  }

  /**
   * Implement a UNARY operation
   */
  @Override
  public Mono<HelloResponse> greet(Mono<HelloRequest> request) {
    return request
      .map(HelloRequest::getName)
      .map(name -> "Hello " + name)
      .map(greeting -> HelloResponse.newBuilder().setMessage(greeting).build());
  }

  /**
   * Implement a STREAMING RESPONSE operation
   */
  @Override
  public Flux<HelloResponse> multiGreet(Mono<HelloRequest> request) {
    return request
      .map(HelloRequest::getName)
      .flatMapMany(
        name -> Flux.just("Welcome", "Hola", "Bonjour").map(salutation -> Tuples.of(name, salutation))
      )
      .map(tuple -> tuple.getT1() + " " + tuple.getT2())
      .map(greeting -> HelloResponse.newBuilder().setMessage(greeting).build());
  }

  /**
   * Implement a BI-DIRECTIONAL STREAMING operation
   */
  @Override
  public Flux<HelloResponse> streamGreet(Flux<HelloRequest> request) {
    return request
      .map(HelloRequest::getName)
      .map(name -> "Greetings " + name)
      .map(greeting -> HelloResponse.newBuilder().setMessage(greeting).build());
  }
}
