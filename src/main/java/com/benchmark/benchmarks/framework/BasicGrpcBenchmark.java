package com.benchmark.benchmarks.framework;

import com.benchmark.benchmarks.impl.GRPCBatchStreamingBench;
import com.benchmark.benchmarks.impl.GRPCPingPongBench;
import com.benchmark.client.GrpcClient;
import com.benchmark.domain.Trade;
import com.benchmark.grpc.servers.GrpcClientServerUtils;
import com.benchmark.grpc.servers.GrpcTradeService;
import com.benchmark.service.CommonTradeService;
import io.grpc.Server;

import java.io.IOException;
import java.util.List;

public class BasicGrpcBenchmark implements Benchmark {

    private Server grpcServer;
    private GrpcClient grpcClient;

    private GrpcTradeService grpcTradeService;

    private final GRPCBatchStreamingBench grpcBatchStreamingBench = new GRPCBatchStreamingBench();
    private final GRPCPingPongBench grpcPingPongBench = new GRPCPingPongBench();

    @Override
    public void initServer(int port, CommonTradeService commonTradeService) {
        grpcTradeService = new GrpcTradeService(commonTradeService);
        grpcServer = GrpcClientServerUtils.createServer(port, grpcTradeService);
        try {
            grpcServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initServer(int port, CommonTradeService commonTradeService, GrpcClientServerUtils.GRPCParams params) {
        grpcTradeService = new GrpcTradeService(commonTradeService);
        grpcServer = GrpcClientServerUtils.createServer(port, grpcTradeService, params);
        try {
            grpcServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initClient(String host, int port) {
        grpcClient = new GrpcClient(GrpcClientServerUtils.createClient(host, port));
    }

    public void initClient(String host, int port, GrpcClientServerUtils.GRPCParams params) {
        grpcClient = new GrpcClient(GrpcClientServerUtils.createClient(host, port, params));
    }

    public void initServerAndClient(String host, int port, CommonTradeService commonTradeService, GrpcClientServerUtils.GRPCParams params) {
        initServer(port, commonTradeService, params);
        initClient(host, port, params);
    }

    @Override
    public void loadTrades(List<Trade> trades, int size) {
        grpcBatchStreamingBench.run(trades, size, grpcClient, grpcTradeService);
    }

    @Override
    public int pingPong(int iters, int initVal) {
        return grpcPingPongBench.run(initVal, grpcClient, iters);
    }

    @Override
    public void stopClient() {
        grpcClient.shutdown();
    }

    @Override
    public void stopServer() {
        grpcServer.shutdown();
    }
}
