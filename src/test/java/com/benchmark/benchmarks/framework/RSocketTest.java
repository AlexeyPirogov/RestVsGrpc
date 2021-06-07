package com.benchmark.benchmarks.framework;

import com.benchmark.benchmarks.rsocket.RSocketServer;
import com.benchmark.client.RSocketClient;
import com.benchmark.grpc.ProtoTrade;
import com.benchmark.service.CommonTradeService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

public class RSocketTest {

    private static final int N = 10_000;

    @Test
    public void test() throws InterruptedException {
        CommonTradeService commonTradeService = new CommonTradeService();
        commonTradeService.initTrades(N);

        int port = 9999;
        RSocketServer rSocketServer = new RSocketServer(port, commonTradeService);
        RSocketClient rSocketClient = new RSocketClient("localhost", port);

        Flux<ProtoTrade> dataStream = rSocketClient.getDataStream();
        System.out.println();
        dataStream.subscribe(new Consumer<ProtoTrade>() {
            @Override
            public void accept(ProtoTrade protoTrade) {
                System.out.println(protoTrade.getId());
            }
        });

        System.out.println("Done");
        Thread.sleep(100_000);
    }
}
