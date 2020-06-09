package com.benchmark.client;

import com.benchmark.grpc.PingRequest;
import com.benchmark.grpc.ProtoTrade;
import com.benchmark.grpc.QueryRequest;
import com.benchmark.grpc.TradeServiceGrpc;
import io.grpc.ManagedChannel;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class GrpcClient {

    private final ManagedChannel channel;
    private final TradeServiceGrpc.TradeServiceBlockingStub stub;

    public GrpcClient(ManagedChannel channel) {
        this.channel = channel;
        stub = TradeServiceGrpc.newBlockingStub(channel);

    }

    //we should return common trades
    public Iterator<ProtoTrade> loadTradesIterator(int n) {
        QueryRequest req = QueryRequest.newBuilder().setNumberOfTrades(n).build();
        return stub.getTrades(req);
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
