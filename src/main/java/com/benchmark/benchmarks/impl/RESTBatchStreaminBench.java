package com.benchmark.benchmarks.impl;

import com.benchmark.domain.Trade;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class RESTBatchStreaminBench {
    public void run(List<Trade> restTrades, String host, int port, int numberOfTrades, HttpClient httpClient, ObjectMapper mapper) {
        URI uri = URI.create("http://" + host + ":" + port + "/getTrades?size=" + numberOfTrades);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .build();

        try {
            HttpResponse<InputStream> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            InputStream jsonStream = httpResponse.body();

            JsonFactory jsonFactory = new JsonFactory();
            try (JsonParser jsonParser = jsonFactory.createParser(jsonStream)) {
                if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
                    throw new IllegalStateException("Expected content to be an array");
                }

                int i = 0;
                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                    Trade trade = mapper.readValue(jsonParser, Trade.class);
                    restTrades.set(i, trade);
                    i++;
                }
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Unable to get status event stream", e);
        }
    }
}
