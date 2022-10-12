package demo.hello.grpc;

import demo.proto.GreeterGrpc;
import demo.proto.HelloRequest;
import demo.proto.HelloResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This client demonstrates calling unary and streaming response operations with the gRPC blocking API.
 *
 * @author aaronchenwei
 */
public class GrpcSyncClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) throws Exception {
    ManagedChannel channel = ManagedChannelBuilder
      .forAddress("localhost", 8888)
      .usePlaintext()
      .build();
    GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);

    /*
     * Create a service request
     */
    HelloRequest request = HelloRequest.newBuilder().setName("World").build();

    /*
     * Call a blocking UNARY operation
     */
    HelloResponse response = blockingStub.greet(request);
    LOGGER.atInfo().log("{}", response.getMessage());

    /*
     * Call a blocking STREAMING RESPONSE operation
     */
    blockingStub.multiGreet(request).forEachRemaining(r -> {
      LOGGER.atInfo().log("{}", r.getMessage());
    });

    // shutdown
    channel.shutdownNow().awaitTermination(30, TimeUnit.SECONDS);
  }
}
