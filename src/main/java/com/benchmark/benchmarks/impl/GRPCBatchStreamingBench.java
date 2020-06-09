package com.benchmark.benchmarks.impl;

import com.benchmark.client.GrpcClient;
import com.benchmark.domain.Trade;
import com.benchmark.grpc.ProtoTrade;
import com.benchmark.grpc.servers.GrpcTradeService;

import java.util.Iterator;
import java.util.List;

public class GRPCBatchStreamingBench {

    public void run(List<Trade> trades, int N, GrpcClient grpcClient, GrpcTradeService grpcTradeService) {
        int i = 0;
        Iterator<ProtoTrade> protoTradeIterator = grpcClient.loadTradesIterator(N);
        while (protoTradeIterator.hasNext()) {
            ProtoTrade protoTrade = protoTradeIterator.next();
            trades.set(i, grpcTradeService.convertToTrade(protoTrade));
            i++;
        }
    }
}
