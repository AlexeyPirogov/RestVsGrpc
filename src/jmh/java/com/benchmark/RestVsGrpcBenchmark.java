package com.benchmark;

import com.benchmark.benchmarks.framework.BasicGrpcBenchmark;
import com.benchmark.benchmarks.framework.BasicRestBenchmark;
import com.benchmark.benchmarks.framework.Benchmark;
import com.benchmark.domain.Trade;
import com.benchmark.service.CommonTradeService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
public class RestVsGrpcBenchmark {

    public static final int N = 10_000;

    private static final CommonTradeService commonTradeService;

    static {
        commonTradeService = new CommonTradeService();
        commonTradeService.initTrades(N);
    }

    @Param(value = {"BasicGrpcBenchmark", "BasicRestBenchmark"})
    private String implementation;

    private Benchmark benchmark;

    @State(Scope.Benchmark)
    public static class BenchState {
        private final List<Trade> trades = new ArrayList<>(Collections.nCopies(N, null));
    }

    @Setup
    public void prepare() {
        benchmark = createBenchmark(implementation);
        benchmark.initServerAndClient("localhost", 8080, commonTradeService);
    }

    private Benchmark createBenchmark(String implementation) {
        return switch (implementation) {
            case "BasicRestBenchmark" -> new BasicRestBenchmark();
            case "BasicGrpcBenchmark" -> new BasicGrpcBenchmark();
            default -> throw new IllegalStateException("Unexpected value: " + implementation);
        };
    }

    @org.openjdk.jmh.annotations.Benchmark
    @Threads(1)
    public void loadTrades(BenchState state) {
        benchmark.loadTrades(state.trades, N);
    }

    @org.openjdk.jmh.annotations.Benchmark
    @Threads(1)
    public void pingPong() {
        benchmark.pingPong(100, 1);
    }

    @TearDown
    public void clean() {
        benchmark.stopClientAndServer();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RestVsGrpcBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}