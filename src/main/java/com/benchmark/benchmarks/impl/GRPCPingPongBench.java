package com.benchmark.benchmarks.impl;

import com.benchmark.client.GrpcClient;

public class GRPCPingPongBench {

    public int run(int pingVal, GrpcClient grpcClient, int iters) {
        int res = pingVal;
        for (int i = 0; i < iters; i++)
            res = grpcClient.ping(res);

        return res;
    }
}
