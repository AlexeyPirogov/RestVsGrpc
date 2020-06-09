package com.benchmark.jetty;

import com.benchmark.domain.Trade;
import com.benchmark.service.CommonTradeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

//todo: should we try blocking?
public class NOT_USED_AsyncTradeHandler extends AbstractHandler {

    private final CommonTradeService commonTradeService;

    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    public NOT_USED_AsyncTradeHandler(CommonTradeService commonTradeService) {
        this.commonTradeService = commonTradeService;
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper om = new ObjectMapper();
//        om.enable(SerializationFeature.EAGER_SERIALIZER_FETCH);
        return om;
    }


    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        int size = Integer.parseInt(baseRequest.getParameter("size"));
        List<Trade> trades = commonTradeService.getTrades(size);

        response.setBufferSize(1024);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Transfer-Encoding", "chunked"); //is required

        baseRequest.setHandled(true);
        response.setStatus(HttpServletResponse.SC_OK);

//        writeJson(trades, response.getWriter());
        writeJson(trades, response.getOutputStream());

    }

    private void writeJson(List<Trade> trades, PrintWriter writer) {
        try {
            writer.print('[');
            for (Trade trade : trades) {
                OBJECT_MAPPER.writeValue(writer, trade);
//                TimeUnit.SECONDS.sleep(5);
            }
            writer.print(']');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeJson(List<Trade> trades, OutputStream outputStream) {
        try {
            outputStream.write('[');
            for (Trade trade : trades) {
                outputStream.write(OBJECT_MAPPER.writeValueAsBytes(trade));
                OBJECT_MAPPER.writeValue(outputStream, trade);
//                OBJECT_MAPPER.writeValue(outputStream, trade);
//                TimeUnit.SECONDS.sleep(1);
            }
            outputStream.write(']');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void writeJson(List<Trade> trades, PrintWriter stream) {
//        JsonFactory jfactory = new JsonFactory();
//        JsonGenerator jGenerator = null;
//        try {
//            jGenerator = jfactory.createGenerator(stream);
////            jGenerator = jfactory.createGenerator(stream, JsonEncoding.UTF8);
//            jGenerator.writeStartArray();
//            for (Trade trade : trades) {
//                jGenerator.writeObject(trade);
////                jGenerator.flush();??
//            }
//
//            jGenerator.writeEndArray();
//            jGenerator.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


}