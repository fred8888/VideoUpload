package com.videoupload.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class VideoHTTPServer {
        public static void main(String[] args)
       {
            try {
                HttpServer httpServer = HttpServer.create(new InetSocketAddress(8888),0);
                httpServer.createContext("/upload", new VideoHandler());

                httpServer.setExecutor(Executors.newCachedThreadPool());
                httpServer.start();

            } catch (Exception e){
                e.printStackTrace();
            }
      }
}



