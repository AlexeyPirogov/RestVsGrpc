package com.benchmark.benchmarks.framework;

import com.benchmark.benchmarks.impl.RESTBatchStreaminBench;
import com.benchmark.benchmarks.impl.RESTPingPongBench;
import com.benchmark.domain.Trade;
import com.benchmark.jetty.AsyncTradeServlet;
import com.benchmark.jetty.JettyServer;
import com.benchmark.service.CommonTradeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.util.List;

public class BasicRestBenchmark implements Benchmark {

    private final ObjectMapper mapper = new ObjectMapper();

    private JettyServer jettyServer;
    private HttpClient httpClient;
    private String host;
    private int port;

    private final RESTBatchStreaminBench RESTBatchStreaminBench = new RESTBatchStreaminBench();
    private final RESTPingPongBench restPingPongBench = new RESTPingPongBench();

    @Override
    public void initServer(int port, CommonTradeService commonTradeService) {
        jettyServer = new JettyServer();
        try {
            jettyServer.start(port, commonTradeService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initClient(String host, int port) {
        this.host = host;
        this.port = port;
        httpClient = HttpClient.newHttpClient();
    }

    @Override
    public void loadTrades(List<Trade> trades, int size) {
        RESTBatchStreaminBench.run(trades, host, port, size, httpClient, mapper);
    }

    @Override
    public int pingPong(int iters, int initVal) {
        return restPingPongBench.run(initVal, host, port, httpClient, iters);
    }

    @Override
    public void stopServer() {
        jettyServer.shutdown();
    }

    @Override
    public void stopClient() { }
}
