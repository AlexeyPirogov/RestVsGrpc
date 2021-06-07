package com.benchmark.benchmarks.impl;

import com.benchmark.client.GrpcClient;
import com.benchmark.domain.Trade;
import com.benchmark.grpc.ProtoTrade;
import com.benchmark.grpc.TradeBatch;
import com.benchmark.grpc.servers.GrpcTradeService;

import java.util.Iterator;
import java.util.List;

public class GRPCBatchStreamingBench {

    public void run(List<Trade> trades, int N, GrpcClient grpcClient, GrpcTradeService grpcTradeService) {
        int i = 0;
        Iterator<TradeBatch> protoTradeIterator = grpcClient.loadTradesBatchesIterator(N);
        while (protoTradeIterator.hasNext()) {
            TradeBatch tradeBatch = protoTradeIterator.next();
            for (int j = 0; j < tradeBatch.getTradesCount(); j++) {
                trades.set(i, grpcTradeService.convertToTrade(tradeBatch.getTrades(j)));
            }
            i++;
        }
    }
}
