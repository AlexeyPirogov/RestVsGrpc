package com.benchmark.jetty;

import com.benchmark.domain.Trade;
import com.benchmark.service.CommonTradeService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class AsyncTradeServlet extends HttpServlet {

    //todo: check if it matters
    public static final int BUFFER_SIZE = 16_000;

    public static CommonTradeService commonTradeService;

    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AsyncContext async = request.startAsync();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Transfer-Encoding", "chunked");

        int size = Integer.parseInt(request.getParameter("size"));
        List<Trade> trades = commonTradeService.getTrades(size);
        Iterator<Trade> iterator = trades.iterator();

        ServletOutputStream out = response.getOutputStream();

        ByteArrayOutputStream bos = new ByteArrayOutputStream(BUFFER_SIZE);
        JsonGenerator jsonGenerator = OBJECT_MAPPER.getFactory().createGenerator(bos);
        jsonGenerator.writeStartArray();
        out.setWriteListener(new WriteListener() {
            @Override
            public void onWritePossible() throws IOException {
                while (out.isReady() && iterator.hasNext()) {
                    Trade trade = iterator.next();
                    try {
                        OBJECT_MAPPER.writeValue(jsonGenerator, trade);
                        out.write(bos.toByteArray());
                        bos.reset();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }

                boolean readyToClose = out.isReady() && !iterator.hasNext();
                if (readyToClose) {
                    jsonGenerator.writeEndArray();
                    jsonGenerator.close();
                    out.write(bos.toByteArray());
                    out.close();
                    response.setStatus(200);
                    async.complete();
                }
            }


            @Override
            public void onError(Throwable t) {
                getServletContext().log("Async Error", t);
                async.complete();
            }
        });
    }
}