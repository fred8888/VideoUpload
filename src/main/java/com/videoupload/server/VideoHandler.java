package com.videoupload.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

class VideoHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        String rootPath = "C:/temp/";
        String filePath = VideoUpload.fileUpload(t, rootPath);

        File videoFile = new File(filePath);

        VideoMetaData metaData = VideoUtil.getVideoMetaData(videoFile);
        String response = metaData.toString();

        try {
            t.sendResponseHeaders(200, response.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
