package com.benchmark.jetty;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class PingPongServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int val = Integer.parseInt(request.getParameter("val"));
        PrintWriter writer = response.getWriter();
        writer.print(val+1);
        writer.flush();//check if required
        writer.close();
    }
}
