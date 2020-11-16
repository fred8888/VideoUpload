package com.videoupload.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;


public class VideoUpload {
    private static RequestFile requestFile;
    private VideoUpload(){};
    public static class RequestFile{
        public String fileName;
        public String boundary;
        public String countentType;
    }
    public static class RequestBody{
        public static String boundary;
        public static List<Map<String, File>> fileItems = new ArrayList<Map<String,File>>();
    }
    public static String fileUpload(HttpExchange httpExchange, String rootPath){

        String filePath = "";
        try{
            InputStream inputStream = httpExchange.getRequestBody();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[1024];
            int length = 0;
            File file;
            OutputStream os = null;
            boolean isStart = true;
            while((length = bufferedInputStream.read(buffer)) != -1 ){
                if( isStart ) {
                    int startReadIndex =getReadIndex(buffer);
                    isStart = false;
                    if(requestFile != null && requestFile.fileName != null){
                        filePath = rootPath + requestFile.fileName;
                        file = new File(filePath);

                        File fileParent = file.getParentFile();
                        if(!fileParent.exists()){
                            fileParent.mkdirs();
                        }

                        if( !file.exists() ) file.createNewFile();
                        os = new FileOutputStream(file);
                        byte[] realData = getTheByte(buffer, startReadIndex, buffer.length - startReadIndex);
                        if(new String(realData).contains("-")){
                            realData = getTheByte(realData, 0, getIndex(realData, "-".getBytes()) - "\r\n-".getBytes().length);
                            os.write(realData);
                            os.close();
                            break;
                        }
                        os.write(buffer, startReadIndex, buffer.length - startReadIndex);
                        continue;
                    }
                }
                if(os == null) break;
                if( !new String(buffer).contains(requestFile.boundary) ){
                    os.write(buffer, 0, length);
                }else{
                    buffer = getTheByte(buffer, 0, getIndex(buffer, (requestFile.boundary).getBytes()) - 2);
                    os.write( buffer, 0, buffer.length - 1);
                    os.close();
                    break;
                }
            }
        }catch(Exception e){ e.printStackTrace(); }


        return filePath;
    }
    private static int getReadIndex(byte[] buffer) {
        try{
            for( int i=0;i<buffer.length; i++){
                int startIndex = getTheLineIndex(buffer, 4 * i);
                int endIndex = getTheLineIndex(buffer, 4 * (i+1));
                byte[] fourLines = getTheByte(buffer, startIndex, endIndex);
                if(checkFileBoundary(fourLines))
                    return endIndex;
            }
        }catch(Exception e){ System.out.println("出现异常:" + e.getMessage()); }
        return 0;
    }

    private static boolean checkFileBoundary(byte[] buffer){
        String fileItem = new String(buffer);
        if(fileItem.contains("filename")) {
            if(requestFile == null) requestFile = new RequestFile();
            requestFile.boundary  = fileItem.substring(0,fileItem.indexOf("\n") - 1);
            fileItem = fileItem.substring(fileItem.indexOf("\n") + 1,fileItem.length());
            requestFile.fileName = fileItem.substring(fileItem.indexOf("filename=\"") + "filename=\"".length(), fileItem.indexOf("\n") - "\"\n".length());
            requestFile.countentType = fileItem.substring(fileItem.indexOf("Content-Type:"),fileItem.length());
            return true;
        }
        return false;
    }
    public static int getTheLineIndex(byte[] source,int lineNumber){
        if(lineNumber <= 0) return 0;
        int lineCount = 0;
        for( int k = 0;k < source.length;k++){
            if( lineCount == lineNumber )
                return k;
            if( source[k] == "\n".getBytes()[0] && lineCount  <= lineNumber)
                lineCount ++;
        }
        return 0;
    }

    public static byte[] getTheByte(byte[] source,int beginIndex,int endIndex){
        if(source == null || source.length <= 0 || endIndex - beginIndex <= 0) return null;
        int byteLength = (endIndex + 1) - beginIndex;
        byte[] temp = new byte[ byteLength ];
        for( int i = 0; i < byteLength; i++ ){ temp[i] = source[i + beginIndex]; }
        return temp;
    }

    public static int getIndex(byte[] source ,byte[] part){
        if (source == null || part == null || source.length == 0 || part.length == 0)
            return -1;
        int i,j;
        for(i=0;i<source.length;i++){
            if(source[i] == part[0]){
                for(j=0;j<part.length;j++)
                    if(source[i+j] != part[j]) break;
                if(j == part.length) return i;
            }
        }
        return -1;
    }


}
