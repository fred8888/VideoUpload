package com.videoupload.client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadClient {
    private static String filePath = "D:/youtube/cindyy.mp4";
    private static String endPoint = "http://127.0.0.1:8888/upload";


    public static void main(String[] args) {
        UploadClient.postFile(filePath);
    }

    public static void postFile(String filePath) {
        DataOutputStream out = null;
        final String newLine = "\r\n";
        final String prefix = "--";
        try {
            URL url = new URL(endPoint);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            String BOUNDARY = "-------7ba2e546606c9";
            conn.setRequestMethod("POST");

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            out = new DataOutputStream(conn.getOutputStream());

            // add parameter file
            File file = new File(filePath);
            StringBuilder sb1 = new StringBuilder();
            sb1.append(prefix);
            sb1.append(BOUNDARY);
            sb1.append(newLine);
            sb1.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"" + newLine);
            sb1.append("Content-Type:application/octet-stream");
            sb1.append(newLine);
            sb1.append(newLine);
            out.write(sb1.toString().getBytes());
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            byte[] bufferOut = new byte[1024];
            int bytes = 0;
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            out.write(newLine.getBytes());
            in.close();


            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            // output ending
            out.write(end_data);
            out.flush();
            out.close();

            //get response from server
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (Exception e) {
            System.out.println("Error!" + e);
            e.printStackTrace();
        }
    }
}
