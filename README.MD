### REST vs GRPC benchmark
In this project we compare REST vs GRPC performance.

##### Scenarios
1. Batch Streaming.   
Generate thousands of objects and send to the client;
2. Ping Pong (round-trip latency);  
Client sends int number, servers receives and sends incremented value in response.
Repeat round-trip multiple times;

##### Benchmarks
com.benchmark.RestVsGrpcBenchmark - REST vs GRPC in streaming and ping-pong benchmark;
```
Benchmark                         (implementation)  Mode  Cnt    Score    Error  Units
RestVsGrpcBenchmark.loadTrades  BasicGrpcBenchmark  avgt    5  149.167 ± 16.678  ms/op
RestVsGrpcBenchmark.loadTrades  BasicRestBenchmark  avgt    5   38.115 ±  1.301  ms/op
RestVsGrpcBenchmark.pingPong    BasicGrpcBenchmark  avgt    5    8.692 ±  0.822  ms/op
RestVsGrpcBenchmark.pingPong    BasicRestBenchmark  avgt    5   17.536 ±  1.603  ms/op

```

com.benchmark.GrpcParamsBenchmark - benchmark to find best GRPC params (e.g. flow window, executor type)
```
Benchmark                       (directExecutor)  (flowWindowSize)  Mode  Cnt    Score    Error  Units
GrpcParamsBenchmark.loadTrades              true             SMALL  avgt    5  161.212 ± 11.688  ms/op
GrpcParamsBenchmark.loadTrades              true            MEDIUM  avgt    5  154.572 ± 15.334  ms/op
GrpcParamsBenchmark.loadTrades              true           DEFAULT  avgt    5  156.426 ± 20.640  ms/op
GrpcParamsBenchmark.loadTrades              true             LARGE  avgt    5  149.111 ±  5.472  ms/op
GrpcParamsBenchmark.loadTrades              true             JUMBO  avgt    5  141.493 ±  9.256  ms/op
GrpcParamsBenchmark.loadTrades             false             SMALL  avgt    5  134.679 ± 36.861  ms/op
GrpcParamsBenchmark.loadTrades             false            MEDIUM  avgt    5  133.460 ± 40.787  ms/op
GrpcParamsBenchmark.loadTrades             false           DEFAULT  avgt    5  129.363 ± 27.624  ms/op
GrpcParamsBenchmark.loadTrades             false             LARGE  avgt    5  119.024 ±  7.472  ms/op
GrpcParamsBenchmark.loadTrades             false             JUMBO  avgt    5  121.343 ± 10.230  ms/op
GrpcParamsBenchmark.pingPong                true             SMALL  avgt    5    8.496 ±  0.369  ms/op
GrpcParamsBenchmark.pingPong                true            MEDIUM  avgt    5    8.587 ±  0.877  ms/op
GrpcParamsBenchmark.pingPong                true           DEFAULT  avgt    5    8.378 ±  1.001  ms/op
GrpcParamsBenchmark.pingPong                true             LARGE  avgt    5    8.560 ±  0.301  ms/op
GrpcParamsBenchmark.pingPong                true             JUMBO  avgt    5    8.441 ±  1.610  ms/op
GrpcParamsBenchmark.pingPong               false             SMALL  avgt    5   10.595 ±  1.832  ms/op
GrpcParamsBenchmark.pingPong               false            MEDIUM  avgt    5   10.979 ±  1.808  ms/op
GrpcParamsBenchmark.pingPong               false           DEFAULT  avgt    5   10.191 ±  0.714  ms/op
GrpcParamsBenchmark.pingPong               false             LARGE  avgt    5   10.995 ±  1.676  ms/op
GrpcParamsBenchmark.pingPong               false             JUMBO  avgt    5   10.859 ±  0.716  ms/op
```

##### Libraries
REST: Server(Jetty + Servlet, Jackson), Client (java.net.http.HttpClient);  
GRPC: GRPC + Netty;

##### How to run benchmarks
JDK 14 is required.
```
mvn clean package

#RestVsGrpcBenchmark
java -jar target/jmhRestVsGRPC.jar

#GrpcParamsBenchmark
java -jar jmhGRPCParams.jar
```
