package com.benchmark.grpc.servers;

import io.grpc.CompressorRegistry;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;

public class GrpcClientServerUtils {

    private static final int WINDOW_SIZE = 128_000;
    //    private static final int WINDOW_SIZE = 64_000;
//    private static final int WINDOW_SIZE = 16_000;
    private static final int channelCount = 4;

    public enum FlowWindowSize {
        SMALL(16383), MEDIUM(65535), DEFAULT(131070), LARGE(1048575), JUMBO(8388607);

        private final int bytes;
        FlowWindowSize(int bytes) {
            this.bytes = bytes;
        }

        public int bytes() {
            return bytes;
        }
    }

    public static class GRPCParams {
        private final FlowWindowSize flowWindowSize;
        private final boolean directExecutor;

        public GRPCParams(FlowWindowSize flowWindowSize, boolean directExecutor) {
            this.flowWindowSize = flowWindowSize;
            this.directExecutor = directExecutor;
        }

        public FlowWindowSize getFlowWindowSize() {
            return flowWindowSize;
        }

        public boolean isDirectExecutor() {
            return directExecutor;
        }
    }

    private static GRPCParams DEFAULT_PARAMS = new GRPCParams(FlowWindowSize.DEFAULT, true);

    public static Server createServer(int port, GrpcTradeService grpcTradeService) {
        return createServer(port, grpcTradeService, DEFAULT_PARAMS);
    }

    public static Server createServer(int port, GrpcTradeService grpcTradeService, GRPCParams grpcParams) {
        //        LocalAddress localAddress = new LocalAddress("grpc");

        Server server;
        NettyServerBuilder serverBuilder;
        serverBuilder = NettyServerBuilder.forPort(port).addService(grpcTradeService);

        if(grpcParams.isDirectExecutor())
            serverBuilder.directExecutor();
        serverBuilder.flowControlWindow(grpcParams.getFlowWindowSize().bytes());
        serverBuilder.compressorRegistry(CompressorRegistry.newEmptyInstance());

        // Always set connection and stream window size to same value

//        // Always use a different worker group from the client.
//        ThreadFactory serverThreadFactory = new DefaultThreadFactory("STF pool", true /* daemon */);
//        serverBuilder.workerEventLoopGroup(new NioEventLoopGroup(0, serverThreadFactory));
//        serverBuilder.bossEventLoopGroup(new NioEventLoopGroup(1, serverThreadFactory));
//ki
////        ManagedChannel[] channels = new ManagedChannel[channelCount];
//        ThreadFactory clientThreadFactory = new DefaultThreadFactory("CTF pool", true /* daemon */);
//        channelBuilder.eventLoopGroup(new NioEventLoopGroup(1, clientThreadFactory));
////        for (int i = 0; i < channelCount; i++) {
////            // Use a dedicated event-loop for each channel
////            channels[i] = channelBuilder
////                    .eventLoopGroup(new NioEventLoopGroup(1, clientThreadFactory))
////                    .clientChannel();
////        }

        return serverBuilder.build();
    }

    public static ManagedChannel createClient(String host, int port) {
        return createClient(host, port, DEFAULT_PARAMS);
    }

    public static ManagedChannel createClient(String host, int port, GRPCParams grpcParams) {
        NettyChannelBuilder channelBuilder;
        channelBuilder = NettyChannelBuilder.forAddress(host, port).usePlaintext();
        if (grpcParams.isDirectExecutor())
            channelBuilder.directExecutor();
        channelBuilder.flowControlWindow(grpcParams.getFlowWindowSize().bytes());

        channelBuilder.compressorRegistry(CompressorRegistry.newEmptyInstance());
        channelBuilder.negotiationType(NegotiationType.PLAINTEXT);
        return channelBuilder.build();
    }

}
