package com.benchmark.grpc.servers;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.NettyServerBuilder;
import io.netty.channel.local.LocalAddress;

import java.io.File;
import java.io.IOException;

public class GRPCServer {

    private final GrpcTradeService grpcTradeService;
    private Server server;

    public GRPCServer(GrpcTradeService grpcTradeService) {
        this.grpcTradeService = grpcTradeService;
    }

    public void init(int port) {
        try {
            //todo: fix path
            File cert = new File("src/main/resources/cert.pem");
            File key = new File("src/main/resources/key.pem");
            server = createNettyServer(port);
//            if (MainApp.ENABLE_HTTPS)
//                server = createSimpleHttpsServer(port, cert, key);
//            else
//                server = createSimpleServer(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Server createNettyServer(int port) throws IOException {
        LocalAddress localAddress = new LocalAddress("grpc");
//        NettyServerBuilder serverBuilder = NettyServerBuilder.forAddress(localAddress);
//        NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(localAddress);
//        nettyChannelBuilder.channelType(LocalChannel.class);
//        serverBuilder.set

        NettyServerBuilder serverBuilder = NettyServerBuilder.forPort(port).addService(grpcTradeService);
//        serverBuilder.channelType(LocalServerChannel.class);
        serverBuilder.directExecutor();

        // Always use a different worker group from the client.
//        ThreadFactory serverThreadFactory = new DefaultThreadFactory("STF pool", true /* daemon */);
//        serverBuilder.workerEventLoopGroup(new NioEventLoopGroup(0, serverThreadFactory));
//        serverBuilder.bossEventLoopGroup(new NioEventLoopGroup(1, serverThreadFactory));

        return serverBuilder.build().start();
    }

    private Server createSimpleServer(int port) throws IOException {
        return ServerBuilder.forPort(port)
                .addService(grpcTradeService)
                .build()
                .start();
    }

    private Server createSimpleHttpsServer(int port, File cert, File key) throws IOException {
        return ServerBuilder.forPort(port)
                .useTransportSecurity(cert, key)
                .addService(grpcTradeService)
                .build()
                .start();
    }

    public void shutdown() {
        server.shutdown();
    }
}
