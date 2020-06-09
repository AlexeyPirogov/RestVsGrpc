package com.benchmark.benchmarks.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RESTPingPongBench {

    public int run(int val, String host, int port, HttpClient httpClient, int iters) {
        int res = val;
        for (int i = 0; i < iters; i++)
            res = makeRequest(res, host, port, httpClient);

        return res;
    }

    private int makeRequest(int val, String host, int port, HttpClient httpClient) {
        URI uri = URI.create("http://" + host + ":" + port + "/ping?val=" + val);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .build();

        try {
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String response = httpResponse.body();
            return Integer.parseInt(response);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Unable to get status event stream", e);
        }
    }
}
