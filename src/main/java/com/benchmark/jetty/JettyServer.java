package com.benchmark.jetty;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class JettyServer {

    private Server server;

    public void start(int port) throws Exception {
        int maxThreads = 100;
        int minThreads = 10;
        int idleTimeout = 120;
        QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);

        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setOutputBufferSize(8100);

        server = new Server(threadPool);
        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        connector.setPort(port);
        server.setConnectors(new Connector[] { connector });

//        server.setHandler(new AsyncTradeHandler(commonTradeService));

        //servlet
        ServletHandler servletHandler = new ServletHandler();
        server.setHandler(servletHandler);
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