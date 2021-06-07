package com.benchmark.benchmarks.framework;

import com.benchmark.benchmarks.rsocket.RSocketServer;
import com.benchmark.client.GrpcClient;
import com.benchmark.client.RSocketClient;
import com.benchmark.domain.Trade;
import com.benchmark.grpc.ProtoTrade;
import com.benchmark.grpc.servers.GrpcTradeService;
import com.benchmark.service.CommonTradeService;
import reactor.core.publisher.Flux;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class BasicRSocketBenchmark implements Benchmark {
    private RSocketServer rSocketServer;
    private RSocketClient rSocketClient;

    private GrpcTradeService grpcTradeService;

    @Override
    public void initServer(int port, CommonTradeService commonTradeService) {
        rSocketServer = new RSocketServer(port, commonTradeService);

        grpcTradeService = new GrpcTradeService(commonTradeService);

    }

    @Override
    public void initClient(String host, int port) {
        rSocketClient = new RSocketClient("localhost", port);
    }

    @Override
    public void loadTrades(List<Trade> trades, int size) {
        Flux<ProtoTrade> dataStream = rSocketClient.getDataStream();

//
//        List<Trade> recievedTrades = dataStream.map(grpcTradeService::convertToTrade).col

        List<Trade> recievedTrades = dataStream.map(grpcTradeService::convertToTrade).collectList().block();
//        for (int i = 0; i < trades.size(); i++) {
//            trades.set(i, recievedTrades.get(i));
//        }


//        final AtomicInteger i = new AtomicInteger();
//        dataStream.subscribe(new Consumer<ProtoTrade>() {
//            @Override
//            public void accept(ProtoTrade protoTrade) {
//                trades.set(i.getAndIncrement(), grpcTradeService.convertToTrade(protoTrade));
//            }
//        });
//        dataStream.blockLast();
    }


    @Override
    public int pingPong(int iters, int initVal) {
        return 0;
    }

    @Override
    public void stopServer() {
        rSocketServer.dispose();
    }

    @Override
    public void stopClient() {
        rSocketClient.dispose();
    }
}
