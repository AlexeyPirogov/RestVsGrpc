package com.benchmark;

import com.benchmark.domain.Trade;
import com.benchmark.grpc.servers.GrpcTradeService;
import com.benchmark.service.CommonTradeService;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
//@Warmup(iterations = 1, timeUnit = TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, timeUnit = TimeUnit.MILLISECONDS)
//@Measurement(iterations = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
public class ProtobuVsJacksonBenchmark {

    public static final int N = 10_000;

    private static final CommonTradeService commonTradeService;
    private static final GrpcTradeService grpcTradeService;

    //move to setup
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writer();
    private static final JsonFactory OBJECT_MAPPER_FACTORY = OBJECT_MAPPER.getFactory();

    static {
        commonTradeService = new CommonTradeService();
        commonTradeService.initTrades(N);
        grpcTradeService = new GrpcTradeService(commonTradeService);
    }
//
//    @Param(value = {
//            "BasicGrpcBenchmark",
//            "BasicRestBenchmark",
//            "BasicRSocketBenchmark"})
//    private String implementation;
//
//    private Benchmark benchmark;

    @State(Scope.Benchmark)
    public static class BenchState {
//        private final List<Trade> trades = new ArrayList<>(Collections.nCopies(N, null));
        private final List<Trade> trades = commonTradeService.getTrades(N);
    }

//    @Setup
//    public void prepare() {
//        benchmark = createBenchmark(implementation);
//        benchmark.initServerAndClient("localhost", 8080, commonTradeService);
//    }
//
//    private Benchmark createBenchmark(String implementation) {
//        return switch (implementation) {
//            case "BasicRestBenchmark" -> new BasicRestBenchmark();
//            case "BasicGrpcBenchmark" -> new BasicGrpcBenchmark();
//            case "BasicRSocketBenchmark" -> new BasicRSocketBenchmark();
//            default -> throw new IllegalStateException("Unexpected value: " + implementation);
//        };
//    }

    @org.openjdk.jmh.annotations.Benchmark
    @Threads(1)
    public byte[] loadTradesProtobuf(BenchState state) throws IOException {
        List<Trade> trades = state.trades;
        ByteArrayOutputStream bos = new ByteArrayOutputStream(100_000);

        for (Trade trade : trades) {
            bos.write(grpcTradeService.convert(trade).toByteArray());
        }
        bos.close();
        return bos.toByteArray();
    }

    @org.openjdk.jmh.annotations.Benchmark
    @Threads(1)
    public byte[] loadTradesJackson(BenchState state) throws IOException {
        List<Trade> trades = state.trades;
        ByteArrayOutputStream bos = new ByteArrayOutputStream(100_000);

        JsonGenerator jsonGenerator = OBJECT_MAPPER_FACTORY.createGenerator(bos);
//        JsonGenerator jsonGenerator = OBJECT_MAPPER.getFactory().createGenerator(bos);
        jsonGenerator.writeStartArray();
        for (Trade trade : trades) {
            OBJECT_WRITER.writeValue(jsonGenerator, trade);
        }
        jsonGenerator.writeEndArray();

//        OBJECT_MAPPER.writeValue(jsonGenerator, trades);


        bos.close();
//        jsonGenerator.close();
        return bos.toByteArray();
    }


//    @TearDown
//    public void clean() {
//        benchmark.stopClientAndServer();
//    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ProtobuVsJacksonBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}