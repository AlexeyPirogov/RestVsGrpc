package com.benchmark;

import com.benchmark.benchmarks.framework.BasicGrpcBenchmark;
import com.benchmark.domain.Trade;
import com.benchmark.grpc.servers.GrpcClientServerUtils;
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
@Warmup(iterations = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
public class GrpcParamsBenchmark {

    public static final int N = 10_000;

    private static final CommonTradeService commonTradeService;

    static {
        commonTradeService = new CommonTradeService();
        commonTradeService.initTrades(N);
    }

    @Param({"true", "false"})
    private boolean directExecutor;

    @Param
    private GrpcClientServerUtils.FlowWindowSize flowWindowSize;

    @Param({"true", "false"})
    private boolean useHttpOk;


    private BasicGrpcBenchmark benchmark;

    @State(Scope.Benchmark)
    public static class BenchState {
        private final List<Trade> trades = new ArrayList<>(Collections.nCopies(N, null));
    }

    @Setup
    public void prepare() {
        benchmark = new BasicGrpcBenchmark();
        GrpcClientServerUtils.GRPCParams params = new GrpcClientServerUtils.GRPCParams(flowWindowSize, directExecutor, useHttpOk);
        benchmark.initServerAndClient("localhost", 8080, commonTradeService, params);
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
                .include(GrpcParamsBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}