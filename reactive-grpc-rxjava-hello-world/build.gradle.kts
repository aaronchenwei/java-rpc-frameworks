import com.google.protobuf.gradle.id

// https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id("java")
  id("idea")
  alias(libs.plugins.protobuf)
}

group = "io.github.aaronchenwei.example"
version = "0.0.1-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(libs.grpcProtobuf)
  implementation(libs.grpcStub)
  runtimeOnly(libs.grpcNetty)

  compileOnly(libs.javaxAnnotationApi)

  implementation(libs.rxgrpcStub)
  implementation(libs.rxjava)

  implementation(libs.slf4jApi)
  runtimeOnly(libs.logbackClassic)

  testImplementation(libs.junitJupiterApi)
  testRuntimeOnly(libs.junitJupiterEngine)
}

protobuf {
  protoc {
    artifact = libs.protoc.get().toString()
  }
  plugins {
    id("grpc") {
      artifact = libs.protocGenGrpcJava.get().toString()
    }
    id("rxgrpc") {
      artifact = libs.rxgrpc.get().toString()
    }
  }
  generateProtoTasks {
    ofSourceSet("main").forEach {
      it.plugins {
        id("grpc") {}
        id("rxgrpc") {}
      }
    }
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
  systemProperty("file.encoding", "UTF-8")
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
  options.encoding = "UTF-8"
}
