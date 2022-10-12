package demo.hello.rx;

import demo.proto.HelloRequest;
import demo.proto.HelloResponse;
import demo.proto.RxGreeterGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This server implements a unary operation, a streaming response operation, and a bi-directional streaming operation.
 *
 * @author aaronchenwei
 */
public class RxGrpcServer extends RxGreeterGrpc.GreeterImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) throws Exception {
    // Start the server
    Server server = ServerBuilder
      .forPort(8888)
      .addService(new RxGrpcServer())
      .build()
      .start();
    LOGGER.atInfo().log("RxGrpcServer has been started.");

    server.awaitTermination();
  }

  /**
   * Implement a UNARY operation
   */
  @Override
  public Single<HelloResponse> greet(Single<HelloRequest> request) {
    return request
      .map(HelloRequest::getName)
      .map(name -> "Hello " + name)
      .map(greeting -> HelloResponse.newBuilder().setMessage(greeting).build());
  }


  /**
   * Implement a STREAMING RESPONSE operation
   */
  @Override
  public Flowable<HelloResponse> multiGreet(Single<HelloRequest> request) {
    return request
      .map(HelloRequest::getName)
      .toFlowable()
      .flatMap(
        x -> Flowable.just("Welcome", "Hola", "Bonjour"),
        (name, salutation) -> salutation + " " + name
      )
      .map(greeting -> HelloResponse.newBuilder().setMessage(greeting).build());
  }


  /**
   * Implement a BI-DIRECTIONAL STREAMING operation
   */
  @Override
  public Flowable<HelloResponse> streamGreet(Flowable<HelloRequest> request) {
    return request
      .map(HelloRequest::getName)
      .map(name -> "Greetings " + name)
      .map(greeting -> HelloResponse.newBuilder().setMessage(greeting).build());
  }
}
