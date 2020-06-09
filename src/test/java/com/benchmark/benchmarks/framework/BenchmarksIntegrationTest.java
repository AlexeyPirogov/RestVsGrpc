package com.benchmark.benchmarks.framework;

import com.benchmark.domain.Trade;
import com.benchmark.service.CommonTradeService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BenchmarksIntegrationTest {

    public static final int N = 10_000;

    private static BasicGrpcBenchmark basicGrpcBenchmark;
    private static BasicRestBenchmark basicRestBenchmark;


    @BeforeAll
    static void init() {
        CommonTradeService commonTradeService = new CommonTradeService();
        commonTradeService.initTrades(N);

        basicGrpcBenchmark = new BasicGrpcBenchmark();
        basicRestBenchmark = new BasicRestBenchmark();
        basicGrpcBenchmark.initServerAndClient("localhost", 8080, commonTradeService);
        basicRestBenchmark.initServerAndClient("localhost", 8081, commonTradeService);
    }

    @Test
    void loadTrades() {
        List<Trade> grpcTrades = new ArrayList<>(Collections.nCopies(N, null));
        List<Trade> restTrades = new ArrayList<>(Collections.nCopies(N, null));
        basicGrpcBenchmark.loadTrades(grpcTrades, N);
        basicRestBenchmark.loadTrades(restTrades, N);

        assertEquals(grpcTrades, restTrades);
    }

    @AfterAll
    static void tearDown() {
        basicGrpcBenchmark.stopClientAndServer();
        basicRestBenchmark.stopClientAndServer();
    }
}