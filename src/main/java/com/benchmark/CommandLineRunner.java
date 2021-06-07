package com.benchmark;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CommandLineRunner.class);

    private enum CLIENT_SERVER_MODE {SERVER, CLIENT, BOTH}

    private enum CLIENT_SERVER_TYPE {GRPC, REST, BOTH}

    private enum BENCHMARK_TYPE {BATCH_STREAMING, PING_PONG, ALL}

    @Option(name = "-m", usage = "client-server mode (server|client|both)")
    private CLIENT_SERVER_MODE clientServerMode = CLIENT_SERVER_MODE.BOTH;

    @Option(name = "-t", usage = "client-server type (grpc|rest|both)")
    private CLIENT_SERVER_TYPE clientServerType = CLIENT_SERVER_TYPE.GRPC;
//    private CLIENT_SERVER_TYPE clientServerType = CLIENT_SERVER_TYPE.BOTH;

    @Option(name = "-benchmark", usage = "benchmark type (batch|pingPong|all)")
//    private BENCHMARK_TYPE benchmarkType = BENCHMARK_TYPE.ALL;
    private BENCHMARK_TYPE benchmarkType = BENCHMARK_TYPE.BATCH_STREAMING;

    @Option(name = "-n", usage = "number of objects in the batch")
    private int batchSize = 10_000;

    @Option(name = "-i", usage = "number of iterations")
    private int iterations = 1;
//    private int iterations = 10;

    @Option(name = "-pingPongIter", usage = "number of ping-pong round-trips per run (100 by default)")
    private int pingPongIters = 100;

    @Option(name = "-h", usage = "host to use (localhost by default)")
    public String host = "localhost";

    @Option(name = "-http_port", usage = "http port (8080 by default)")
    public int httpPort = 8080;

    @Option(name = "-grpc_port", usage = "grpc port (9000 by default)")
    public int grpcPort = 9000;

    public static void main(String[] args) {
        CommandLineRunner commandLineRunner = new CommandLineRunner();
        try {
            commandLineRunner.parseInputs(args);
        } catch (IOException e) {
            LOG.error("Error parsing arguments=" + args, e);
        }
        Runner.RunnerParams runnerParams = commandLineRunner.createRunnerParams();
        LOG.info("Starting perf-tests with next params: " + runnerParams);

        Runner app = new Runner(runnerParams);
        app.initServersAndClients();

        if (runnerParams.isRunServer())
            app.initData();

        if (runnerParams.isRunClient())
            app.benchmark();

        if (runnerParams.isRunServer() && !runnerParams.isRunClient()) { //stand-alone server
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                LOG.info("Server interrupted, stopping server");
                app.stop();
            }
        }

        app.stop();
    }

    //todo: refactor to return input instead of using method with side-effect
    private void parseInputs(String[] args) throws IOException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println("Input error");
            parser.printUsage(System.err);
            System.err.println();
            throw new IOException();
        }
    }

    private Runner.RunnerParams createRunnerParams() {
        Runner.RunnerParams params = new Runner.RunnerParams();
        params.setBatchSize(batchSize);
        params.setPingPongIterations(pingPongIters);
        params.setIterations(iterations);

        switch (clientServerType) {
            case BOTH -> {
                params.setGrpcEnabled(true);
                params.setRestEnabled(true);
            }
            case GRPC -> params.setGrpcEnabled(true);
            case REST -> params.setRestEnabled(true);
        }

        switch (clientServerMode) {
            case BOTH -> {
                params.setRunServer(true);
                params.setRunClient(true);
            }
            case SERVER -> params.setRunServer(true);
            case CLIENT -> params.setRunClient(true);
        }

        switch (benchmarkType) {
            case ALL -> {
                params.setRunBatchStreamingBench(true);
                params.setRunPingPongBench(true);
            }
            case PING_PONG -> params.setRunPingPongBench(true);
            case BATCH_STREAMING -> params.setRunBatchStreamingBench(true);
        }

        params.setHost(host);
        params.setHttpPort(httpPort);
        params.setGrpcPort(grpcPort);

        return params;
    }
}
