package com.benchmark.benchmarks.framework;

import com.benchmark.domain.Trade;
import com.benchmark.service.CommonTradeService;

import java.util.List;

public interface Benchmark {
    void initServer(int port, CommonTradeService commonTradeService);
    void initClient(String host, int port);
    default void initServerAndClient(String host, int port, CommonTradeService commonTradeService) {
        initServer(port, commonTradeService);
        initClient(host, port);
    }

    void loadTrades(List<Trade> trades, int size);
    int pingPong(int iters, int initVal);

    void stopServer();
    void stopClient();
    default void stopClientAndServer() {
        stopClient();
        stopServer();
    }

}
