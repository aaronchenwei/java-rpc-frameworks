/*
   Gradle Version Catalogs is introduced as an experimental feature in version 7.0
   and promoted it to stable in version 7.4.
   Uncomment below line if using gradle version earlier than 7.4
 */
//enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "java-rpc-frameworks"

include("grpc-hello-world")
include("reactive-grpc-rxjava-hello-world")
include("reactive-grpc-reactor-hello-world")
