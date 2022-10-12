package demo.hello.rx;

import demo.proto.HelloRequest;
import demo.proto.HelloResponse;
import demo.proto.RxGreeterGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aaronchenwei
 */
public class RxGrpcAsyncClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) throws Exception {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8888).usePlaintext().build();
    RxGreeterGrpc.RxGreeterStub stub = RxGreeterGrpc.newRxStub(channel);

    /*
     * Create a service request
     */
    Single<HelloRequest> request = Single.just(HelloRequest.newBuilder().setName("World").build());

    /*
     * Call an async UNARY operation
     */
    request
      // Call service
      .as(stub::greet)
      // Map response
      .map(HelloResponse::getMessage)
      .subscribe(s -> LOGGER.atInfo().log("{}", s));

    /*
     * Call an async STREAMING RESPONSE operation
     */
    request
      // Call service
      .as(stub::multiGreet)
      // Map response
      .map(HelloResponse::getMessage)
      .subscribe(s -> LOGGER.atInfo().log("{}", s));

    /*
     * Call an async BI-DIRECTIONAL STREAMING operation
     */
    Flowable
      // Call service
      .just("Alpha", "Beta", "Gamma")
      .map(name -> HelloRequest.newBuilder().setName(name).build())
      .as(stub::streamGreet)
      // Map response
      .map(HelloResponse::getMessage)
      .subscribe(s -> LOGGER.atInfo().log("{}", s));

    Thread.sleep(Duration.ofSeconds(1).toMillis());

    // shutdown
    channel.shutdownNow().awaitTermination(30, TimeUnit.SECONDS);
  }
}
