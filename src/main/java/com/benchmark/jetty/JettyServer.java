package com.benchmark.jetty;

import com.benchmark.service.CommonTradeService;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class JettyServer {

    private Server server;

    public void start(int port, CommonTradeService commonTradeService) throws Exception {
        int maxThreads = 5;
        int minThreads = 5;
        int idleTimeout = 120;
        QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);
        HttpConfiguration httpConfig = new HttpConfiguration();

        server = new Server(threadPool);
        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        connector.setPort(port);
        server.setConnectors(new Connector[] { connector });

        //servlet
        ServletHandler servletHandler = new ServletHandler();
        server.setHandler(servletHandler);
        AsyncTradeServlet.setCommonTradeService(commonTradeService);
        servletHandler.addServletWithMapping(AsyncTradeServlet.class, "/getTrades");
        servletHandler.addServletWithMapping(PingPongServlet.class, "/ping");

        server.start();
    }

    public void shutdown()  {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}