# java-rpc-frameworks <!-- omit in toc -->

## TOC <!-- omit in toc -->

- [1. gRPC](#1-grpc)
  - [1.1. Reactive gRPC](#11-reactive-grpc)

## 1. gRPC

[gRPC](https://grpc.io/) is a modern open source high performance Remote Procedure Call (RPC) framework that can run in any environment. It can efficiently connect services in and across data centers with pluggable support for load balancing, tracing, health checking and authentication. It is also applicable in last mile of distributed computing to connect devices, mobile applications and browsers to backend services.

- [grpc-hello-world](./grpc-hello-world)

### 1.1. Reactive gRPC

Reactive gRPC is a suite of libraries for using gRPC with [Reactive Streams](http://www.reactive-streams.org/) programming libraries. Using a protocol buffers compiler plugin, Reactive gRPC generates alternative gRPC bindings for each reactive technology. The reactive bindings support unary and streaming operations in both directions. Reactive gRPC also builds on top of gRPC's back-pressure support, to deliver end-to-end back-pressure-based flow control in line with Reactive Streams back-pressure model.

Reactive gRPC supports the following reactive programming models: RxJava 2 and Spring Reactor.

- [reactive-grpc-rxjava-hello-world](./reactive-grpc-rxjava-hello-world)
- [reactive-grpc-reactor-hello-world](./reactive-grpc-reactor-hello-world)
