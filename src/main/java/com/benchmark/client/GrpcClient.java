package com.benchmark.client;

import com.benchmark.domain.Trade;
import com.benchmark.grpc.*;
import com.benchmark.grpc.servers.GrpcTradeService;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GrpcClient {

    private final ManagedChannel channel;
    private final TradeServiceGrpc.TradeServiceBlockingStub stub;
    private final TradeServiceGrpc.TradeServiceStub asyncStub;

    public GrpcClient(ManagedChannel channel) {
        this.channel = channel;
        stub = TradeServiceGrpc.newBlockingStub(channel);
        asyncStub = TradeServiceGrpc.newStub(channel);
    }

    //we should return common trades
    public Iterator<ProtoTrade> loadTradesIterator(int n) {
        QueryRequest req = QueryRequest.newBuilder().setNumberOfTrades(n).build();
        return stub.getTrades(req);
    }

    public Iterator<TradeBatch> loadTradesBatchesIterator(int n) {
        QueryRequest req = QueryRequest.newBuilder().setNumberOfTrades(n).build();
        return stub.getTradeBatches(req);
    }

    public void loadTradeAsync(List<Trade> trades, int n, GrpcTradeService grpcTradeService) {
        QueryRequest req = QueryRequest.newBuilder().setNumberOfTrades(n).build();

        CountDownLatch latch = new CountDownLatch(1);

        asyncStub.getTrades(req, new StreamObserver<>() {
            @Override
            public void onNext(ProtoTrade value) {
                trades.add(grpcTradeService.convertToTrade(value));
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public int ping(int n) {
        PingRequest request = PingRequest.newBuilder().setVal(n).build();
        return stub.pingPong(request).getVal();
    }

    public void shutdown() {
        channel.shutdown();
        try {
            channel.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
