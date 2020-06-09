package com.benchmark;

import com.benchmark.benchmarks.framework.BasicGrpcBenchmark;
import com.benchmark.benchmarks.framework.BasicRestBenchmark;
import com.benchmark.domain.Trade;
import com.benchmark.service.CommonTradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Runner {

    private static final Logger LOG = LoggerFactory.getLogger(Runner.class);

    private final RunnerParams params;

    private final CommonTradeService commonTradeService = new CommonTradeService();
    private final BasicGrpcBenchmark grpcBenchmark = new BasicGrpcBenchmark();
    private final BasicRestBenchmark restBenchmark = new BasicRestBenchmark();

    public Runner(RunnerParams params) {
        this.params = params;
    }

    static boolean classFound(String name) {
        try {
            Class.forName(name, /* init = */ false, Runner.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
        }
        return false;
    }

    boolean enableStats = classFound("io.opencensus.impl.stats.StatsComponentImpl")
            || classFound("io.opencensus.impllite.stats.StatsComponentImplLite");

    boolean enableTracing = classFound("io.opencensus.impl.trace.TraceComponentImpl")
            || classFound("io.opencensus.impllite.trace.TraceComponentImplLite");


    public void initData() {
//        commonTradeService.initTradesFromFile(params.getBatchSize());
        commonTradeService.initTrades(10_000);
//        commonTradeService.serializeTrade();
    }

    public void stop() {
        if (params.isGrpcEnabled()) grpcBenchmark.stopClientAndServer();
        if (params.isRestEnabled()) restBenchmark.stopClientAndServer();
    }

    public void initServersAndClients() {
        if (params.isGrpcEnabled())
            grpcBenchmark.initServerAndClient(params.getHost(), params.getGrpcPort(), commonTradeService);
        if (params.isRestEnabled())
            restBenchmark.initServerAndClient(params.getHost(), params.getHttpPort(), commonTradeService);
    }

    public void benchmark() {
        sleep();

        List<Trade> restTrades = new ArrayList<>(Collections.nCopies(params.getBatchSize(), null));
        List<Trade> grpcTrades = new ArrayList<>(Collections.nCopies(params.getBatchSize(), null));

        LOG.info("Starting benchmarks");
        for (int i = 0; i < params.getIterations(); i++) {
            long start;

            if (params.isRunBatchStreamingBench() && params.isGrpcEnabled()) {
                sleepAndGC();
                start = System.currentTimeMillis();
                grpcBenchmark.loadTrades(grpcTrades, params.getBatchSize());
                System.out.println("GRPC took " + (System.currentTimeMillis() - start));
            }

            if (params.isRunBatchStreamingBench() && params.isRestEnabled()) {
                sleepAndGC();
                start = System.currentTimeMillis();
                restBenchmark.loadTrades(restTrades, params.getBatchSize());
                System.out.println("HTTP took " + (System.currentTimeMillis() - start));
            }

            if (params.isRunPingPongBench() && params.isRestEnabled()) {
                sleepAndGC();
                start = System.currentTimeMillis();
                int pingInitVal = 1;
                int pingResponse = restBenchmark.pingPong(params.getPingPongIterations(), pingInitVal);
                System.out.println("HTTP PING took " + (System.currentTimeMillis() - start));
                if (pingResponse != pingInitVal + params.getPingPongIterations())
                    System.out.println("HTTP PING returned wrong result=" + pingResponse);
            }

            if (params.isRunPingPongBench() && params.isGrpcEnabled()) {
                sleepAndGC();
                start = System.currentTimeMillis();
                int pingInitVal = 1;
                int pingResponse = grpcBenchmark.pingPong(params.getPingPongIterations(), pingInitVal);
                System.out.println("GRPC PING took " + (System.currentTimeMillis() - start));
                if (pingResponse != pingInitVal + params.getPingPongIterations())
                    System.out.println("GRPC PING returned wrong result=" + pingResponse);
            }

            if (params.isRunBatchStreamingBench() && params.isGrpcEnabled()
                    && params.isRestEnabled())
                compare(grpcTrades, restTrades);
        }
    }

    private void sleep() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sleepAndGC() {
        try {
            System.gc();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void compare(List<Trade> grpcTrades, List<Trade> restTrades) {
        for (int i = 0; i < params.getBatchSize(); i++) {
            if (!grpcTrades.get(i).equals(restTrades.get(i)))
                System.out.println("NOT EQUAL");
        }
    }

    public static class RunnerParams {
        public int batchSize;
        private int iterations;
        private int pingPongIterations;
        private boolean restEnabled;
        private boolean grpcEnabled;
        public int httpPort;
        public int grpcPort;
        public String host;

        private boolean runServer;
        private boolean runClient;

        private boolean runBatchStreamingBench;
        private boolean runPingPongBench;

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public void setHttpPort(int httpPort) {
            this.httpPort = httpPort;
        }

        public void setGrpcPort(int grpcPort) {
            this.grpcPort = grpcPort;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public boolean isRunServer() {
            return runServer;
        }

        public void setRunServer(boolean runServer) {
            this.runServer = runServer;
        }

        public boolean isRunClient() {
            return runClient;
        }

        public void setRunClient(boolean runClient) {
            this.runClient = runClient;
        }


        public int getIterations() {
            return iterations;
        }

        public void setIterations(int iterations) {
            this.iterations = iterations;
        }


        public int getPingPongIterations() {
            return pingPongIterations;
        }

        public void setPingPongIterations(int pingPongIterations) {
            this.pingPongIterations = pingPongIterations;
        }

        public boolean isRestEnabled() {
            return restEnabled;
        }

        public void setRestEnabled(boolean restEnabled) {
            this.restEnabled = restEnabled;
        }

        public boolean isGrpcEnabled() {
            return grpcEnabled;
        }

        public void setGrpcEnabled(boolean grpcEnabled) {
            this.grpcEnabled = grpcEnabled;
        }

        public int getGrpcPort() {
            return grpcPort;
        }

        public int getHttpPort() {
            return httpPort;
        }

        public String getHost() {
            return host;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public boolean isRunPingPongBench() {
            return runPingPongBench;
        }

        public void setRunPingPongBench(boolean runPingPongBench) {
            this.runPingPongBench = runPingPongBench;
        }

        public boolean isRunBatchStreamingBench() {
            return runBatchStreamingBench;
        }

        public void setRunBatchStreamingBench(boolean runBatchStreamingBench) {
            this.runBatchStreamingBench = runBatchStreamingBench;
        }

        public RunnerParams() {
        }

        @Override
        public String toString() {
            return "RunnerParams{" +
                    "batchSize=" + batchSize +
                    ", iterations=" + iterations +
                    ", pingPongIterations=" + pingPongIterations +
                    ", restEnabled=" + restEnabled +
                    ", grpcEnabled=" + grpcEnabled +
                    ", httpPort=" + httpPort +
                    ", grpcPort=" + grpcPort +
                    ", host='" + host + '\'' +
                    ", runServer=" + runServer +
                    ", runClient=" + runClient +
                    ", runBatchStreamingBench=" + runBatchStreamingBench +
                    ", runPingPongBench=" + runPingPongBench +
                    '}';
        }
    }
}
