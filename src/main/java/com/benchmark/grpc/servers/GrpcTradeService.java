package com.benchmark.grpc.servers;

import com.benchmark.domain.Trade;
import com.benchmark.grpc.*;
import com.benchmark.service.CommonTradeService;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class GrpcTradeService extends TradeServiceGrpc.TradeServiceImplBase {

    private final CommonTradeService commonTradeService;

    public GrpcTradeService(CommonTradeService commonTradeService) {
        this.commonTradeService = commonTradeService;
    }

    @Override
    public void getTrades(com.benchmark.grpc.QueryRequest request,
                          io.grpc.stub.StreamObserver<com.benchmark.grpc.ProtoTrade> responseObserver) {
        int numberOfTrades = request.getNumberOfTrades();
        List<Trade> trades = commonTradeService.getTrades(numberOfTrades);
        for (Trade trade : trades) {
            responseObserver.onNext(convert(trade));
        }
        responseObserver.onCompleted();
    }

    private int batchSize = 500;

    @Override
    public void getTradeBatches(QueryRequest request, StreamObserver<TradeBatch> responseObserver) {
        int numberOfTrades = request.getNumberOfTrades();
        List<Trade> trades = commonTradeService.getTrades(numberOfTrades);

        TradeBatch.Builder tradeBatchBuilder = TradeBatch.newBuilder();
        for (int i = 0; i < numberOfTrades; i++) {
            tradeBatchBuilder.addTrades(convert(trades.get(i)));

            if (i % batchSize == 0 && i > 0) {
                responseObserver.onNext(tradeBatchBuilder.build());
                tradeBatchBuilder = TradeBatch.newBuilder();
            }
        }
        responseObserver.onNext(tradeBatchBuilder.build());

        responseObserver.onCompleted();
    }

    @Override
    public void pingPong(PingRequest request, StreamObserver<PongResponse> responseObserver) {
        int val = request.getVal();
        PongResponse.newBuilder().setVal(val + 1).build();
        responseObserver.onNext(
                PongResponse.newBuilder().setVal(val + 1).build());
        responseObserver.onCompleted();
    }

    public ProtoTrade convert(Trade trade) {
        ProtoTrade.Builder builder = ProtoTrade.newBuilder();

        builder.setId(trade.getId());
        builder.setExternalId(trade.getExternalId());
        builder.setInfo(trade.getInfo());
        builder.setStr1(trade.getStr1());
        builder.setStr2(trade.getStr2());
        builder.setStr3(trade.getStr3());
        builder.setStr4(trade.getStr4());
        builder.setStr5(trade.getStr5());
        builder.setStr6(trade.getStr6());
        builder.setStr7(trade.getStr7());
        builder.setStr8(trade.getStr8());
        builder.setStr9(trade.getStr9());
        builder.setStr10(trade.getStr10());

        builder.setLong1(trade.getLong1());
        builder.setLong2(trade.getLong2());
        builder.setLong3(trade.getLong3());
        builder.setLong4(trade.getLong4());
        builder.setLong5(trade.getLong5());
        builder.setLong6(trade.getLong6());
        builder.setLong7(trade.getLong7());
        builder.setLong8(trade.getLong8());
        builder.setLong9(trade.getLong9());
        builder.setLong10(trade.getLong10());

        return builder.build();
    }

    public Trade convertToTrade(ProtoTrade pt) {
        return new Trade(pt.getId(), pt.getExternalId(), pt.getInfo(),
                pt.getStr1(), pt.getStr2(), pt.getStr3(), pt.getStr4(), pt.getStr5(), pt.getStr6(), pt.getStr7(), pt.getStr8(), pt.getStr9(), pt.getStr10(),
                pt.getLong1(), pt.getLong2(), pt.getLong3(), pt.getLong4(), pt.getLong5(), pt.getLong6(), pt.getLong7(), pt.getLong8(), pt.getLong9(), pt.getLong10());
    }
}
