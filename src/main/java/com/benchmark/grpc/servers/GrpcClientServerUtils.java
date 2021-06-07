package com.benchmark.grpc.servers;

import io.grpc.CompressorRegistry;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.okhttp.OkHttpChannelBuilder;

public class GrpcClientServerUtils {

//    private static final GRPCParams DEFAULT_PARAMS = new GRPCParams(FlowWindowSize.JUMBO, true, true);
    private static final GRPCParams DEFAULT_PARAMS = new GRPCParams(FlowWindowSize.SIMILAR_TO_REST, true, false);
//    private static final GRPCParams DEFAULT_PARAMS = new GRPCParams(FlowWindowSize.SIMILAR_TO_REST, true, true);

    public enum FlowWindowSize {
//        SMALL(16383), MEDIUM(65535),
        SIMILAR_TO_REST(131070);
//        LARGE(1048575),
//        JUMBO(8388607);

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
        private final boolean useHttpOk;


        public GRPCParams(FlowWindowSize flowWindowSize, boolean directExecutor, boolean useHttpOk) {
            this.flowWindowSize = flowWindowSize;
            this.directExecutor = directExecutor;
            this.useHttpOk = useHttpOk;
        }

        public FlowWindowSize getFlowWindowSize() {
            return flowWindowSize;
        }

        public boolean isDirectExecutor() {
            return directExecutor;
        }

        public boolean isUseHttpOk() {
            return useHttpOk;
        }
    }

    public static Server createServer(int port, GrpcTradeService grpcTradeService) {
        return createServer(port, grpcTradeService, DEFAULT_PARAMS);
    }

    public static Server createServer(int port, GrpcTradeService grpcTradeService, GRPCParams grpcParams) {
        NettyServerBuilder serverBuilder = NettyServerBuilder.forPort(port).addService(grpcTradeService);

        if (grpcParams.isDirectExecutor())
            serverBuilder.directExecutor();
        serverBuilder.flowControlWindow(grpcParams.getFlowWindowSize().bytes());
        serverBuilder.compressorRegistry(CompressorRegistry.newEmptyInstance());

        return serverBuilder.build();
    }

    public static ManagedChannel createClient(String host, int port) {
        return createClient(host, port, DEFAULT_PARAMS);
    }

    public static ManagedChannel createClient(String host, int port, GRPCParams grpcParams) {
        if (grpcParams.isUseHttpOk()) {
            System.out.println("Using HTTP OK");
            OkHttpChannelBuilder okHttpChannelBuilder = OkHttpChannelBuilder.forAddress("localhost", port).usePlaintext();
            if (grpcParams.isDirectExecutor())
                okHttpChannelBuilder.directExecutor();
            okHttpChannelBuilder.flowControlWindow(grpcParams.getFlowWindowSize().bytes());
            okHttpChannelBuilder.compressorRegistry(CompressorRegistry.newEmptyInstance());
            return okHttpChannelBuilder.build();
        } else {
            System.out.println("Using Netty");
            NettyChannelBuilder nettyChannelBuilder;
            nettyChannelBuilder = NettyChannelBuilder.forAddress(host, port).usePlaintext();
            if (grpcParams.isDirectExecutor())
                nettyChannelBuilder.directExecutor();
            nettyChannelBuilder.flowControlWindow(grpcParams.getFlowWindowSize().bytes());

            nettyChannelBuilder.compressorRegistry(CompressorRegistry.newEmptyInstance());
            nettyChannelBuilder.negotiationType(NegotiationType.PLAINTEXT);
            return nettyChannelBuilder.build();
        }
    }

}
