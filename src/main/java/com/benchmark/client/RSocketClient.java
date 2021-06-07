package com.benchmark.client;

import com.benchmark.grpc.ProtoTrade;
import com.google.protobuf.InvalidProtocolBufferException;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

public class RSocketClient {

    private static final byte[] STREAM_NAME = "Rocket Stream".getBytes();
    private final RSocket socket;

    public RSocketClient(String host, int port) {
        this.socket = RSocketFactory.connect()
                .transport(TcpClientTransport.create(host, port))
                .start()
                .block();
    }

    public Flux<ProtoTrade> getDataStream() {
        return socket
                .requestStream(DefaultPayload.create(STREAM_NAME))
                .map(Payload::getData)
                .map(buf -> {
                    try {
                        return ProtoTrade.parseFrom(buf);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .onErrorReturn(null);
    }

    public void dispose() {
        this.socket.dispose();
    }
}