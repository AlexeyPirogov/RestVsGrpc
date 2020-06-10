### REST vs GRPC benchmark
In this project we compare REST vs GRPC performance.

##### Scenarios
1. Batch Streaming.   
Generate thousands of objects and send to the client;
2. Ping Pong (round-trip latency);  
Client sends int number, servers receives and sends incremented value in response.
Repeat round-trip multiple times;

#### Important: GRPC Netty implementation is 3-5 times slower than GRPC-HTTPOK implementation  
Batch Streaming benchmark take 149ms for GRPC-Netty, 38ms for Rest and 20ms for GRPC-HttpOk.  
Issue with GRPC-Netty was reported before https://github.com/grpc/grpc-java/issues/6696, but not fixed yet. 


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
Benchmark                       (directExecutor)  (flowWindowSize)  (useHttpOk)  Mode  Cnt    Score     Error  Units
GrpcParamsBenchmark.loadTrades              true             SMALL         true  avgt    3   49.280 ±  11.958  ms/op
GrpcParamsBenchmark.loadTrades              true             SMALL        false  avgt    3  150.784 ±  57.130  ms/op
GrpcParamsBenchmark.loadTrades              true            MEDIUM         true  avgt    3   41.008 ±  12.990  ms/op
GrpcParamsBenchmark.loadTrades              true            MEDIUM        false  avgt    3  145.732 ±  43.919  ms/op
GrpcParamsBenchmark.loadTrades              true   SIMILAR_TO_REST         true  avgt    3   38.896 ±  10.368  ms/op
GrpcParamsBenchmark.loadTrades              true   SIMILAR_TO_REST        false  avgt    3  154.343 ±  98.181  ms/op
GrpcParamsBenchmark.loadTrades             false             SMALL         true  avgt    3   21.456 ±   5.996  ms/op
GrpcParamsBenchmark.loadTrades             false             SMALL        false  avgt    3  136.521 ± 125.297  ms/op
GrpcParamsBenchmark.loadTrades             false            MEDIUM         true  avgt    3   19.387 ±  27.275  ms/op
GrpcParamsBenchmark.loadTrades             false            MEDIUM        false  avgt    3  127.194 ±  17.451  ms/op
GrpcParamsBenchmark.loadTrades             false   SIMILAR_TO_REST         true  avgt    3   19.774 ±  13.754  ms/op
GrpcParamsBenchmark.loadTrades             false   SIMILAR_TO_REST        false  avgt    3  115.922 ±  29.976  ms/op
GrpcParamsBenchmark.pingPong                true             SMALL         true  avgt    3    8.243 ±   4.851  ms/op
GrpcParamsBenchmark.pingPong                true             SMALL        false  avgt    3    7.930 ±   2.121  ms/op
GrpcParamsBenchmark.pingPong                true            MEDIUM         true  avgt    3    8.054 ±   1.959  ms/op
GrpcParamsBenchmark.pingPong                true            MEDIUM        false  avgt    3    7.805 ±   5.538  ms/op
GrpcParamsBenchmark.pingPong                true   SIMILAR_TO_REST         true  avgt    3    7.970 ±   1.221  ms/op
GrpcParamsBenchmark.pingPong                true   SIMILAR_TO_REST        false  avgt    3    7.977 ±   2.290  ms/op
GrpcParamsBenchmark.pingPong               false             SMALL         true  avgt    3   11.057 ±   1.250  ms/op
GrpcParamsBenchmark.pingPong               false             SMALL        false  avgt    3   11.227 ±   6.941  ms/op
GrpcParamsBenchmark.pingPong               false            MEDIUM         true  avgt    3   10.478 ±   4.036  ms/op
GrpcParamsBenchmark.pingPong               false            MEDIUM        false  avgt    3   10.873 ±   5.040  ms/op
GrpcParamsBenchmark.pingPong               false   SIMILAR_TO_REST         true  avgt    3   11.100 ±   0.546  ms/op
GrpcParamsBenchmark.pingPong               false   SIMILAR_TO_REST        false  avgt    3   10.953 ±   2.164  ms/op
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
