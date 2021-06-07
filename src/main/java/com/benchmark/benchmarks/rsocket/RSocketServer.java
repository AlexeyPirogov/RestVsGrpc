package com.benchmark.benchmarks.rsocket;

import com.benchmark.domain.Trade;
import com.benchmark.grpc.servers.GrpcTradeService;
import com.benchmark.service.CommonTradeService;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

public class RSocketServer {
    private final Disposable server;
    private final CommonTradeService commonTradeService;

    public RSocketServer(int port, CommonTradeService commonTradeService) {
        this.server = RSocketFactory.receive()
                .acceptor((setupPayload, reactiveSocket) -> Mono.just(new RSocketImpl()))
                .transport(TcpServerTransport.create("localhost", port))
                .start()
                .subscribe();
        this.commonTradeService = commonTradeService;
    }

    public void dispose() {
        this.server.dispose();
    }


    private class RSocketImpl extends AbstractRSocket {


        @Override
        public Flux<Payload> requestStream(Payload payload) {
            List<Trade> trades = commonTradeService.getTrades(10_000);
            GrpcTradeService grpcTradeService = new GrpcTradeService(commonTradeService);
            Stream<Payload> stream = trades.stream().map(t -> grpcTradeService.convert(t).toByteArray()).map(DefaultPayload::create);



            String streamName = payload.getDataUtf8();
//            if (DATA_STREAM_NAME.equals(streamName)) {
                return Flux.fromStream(stream);
//            }
//            return Flux.error(new IllegalArgumentException(streamName));
        }

    }


}