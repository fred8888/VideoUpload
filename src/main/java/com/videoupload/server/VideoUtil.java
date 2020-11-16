package com.videoupload.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoUtil {
    private static String FFMPEGPATH = "D:\\youtube\\ffmpeg\\ffmpeg_g.exe";
    private static Pattern durationPattern;
    private static Pattern videoPattern;
    private static Pattern audioPattern;
    private static String durationRegex = "Duration: (\\d*?):(\\d*?):(\\d*?)\\.(\\d*?), start: (.*?), bitrate: (\\d*) kb\\/s.*";
    private static String videoRegex = "Stream #\\d:\\d[\\(]??\\S*[\\)]??: Video: (\\S*\\S$?)[^\\,]*, (.*?), (\\d*)x(\\d*)[^\\,]*, (\\d*) kb\\/s, (\\d*[\\.]??\\d*) fps";
    private static String audioRegex = "Stream #\\d:\\d[\\(]??\\S*[\\)]??: Audio: (\\S*\\S$?)(.*), (.*?) Hz, (.*?), (.*?), (\\d*) kb\\/s";;

    static {
        durationPattern = Pattern.compile(durationRegex);
        videoPattern = Pattern.compile(videoRegex);
        audioPattern = Pattern.compile(audioRegex);
    }

    public static String execute(List<String> commonds) {

        LinkedList<String> ffmpegCmds = new LinkedList<>(commonds);
        ffmpegCmds.addFirst(FFMPEGPATH);

        Runtime runtime = Runtime.getRuntime();
        Process ffmpeg = null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(ffmpegCmds);
            ffmpeg = builder.start();

            PrintingStream errorStream = new PrintingStream(ffmpeg.getErrorStream());
            PrintingStream inputStream = new PrintingStream(ffmpeg.getInputStream());
            errorStream.start();
            inputStream.start();

            ffmpeg.waitFor();

            String result = errorStream.stringBuffer.append(inputStream.stringBuffer).toString();

            return result;

        } catch (Exception e) {
            return null;

        } finally {
            if (null != ffmpeg) {
                StopProcess ffmpegKiller = new StopProcess(ffmpeg);
                runtime.addShutdownHook(ffmpegKiller);
            }
        }
    }


    public static VideoMetaData getVideoMetaData(File videoFile) {

        String parseResult = getMetaDataFromFFmpeg(videoFile);

        Matcher durationMacher = durationPattern.matcher(parseResult);
        Matcher videoStreamMacher = videoPattern.matcher(parseResult);
        Matcher videoMusicStreamMacher = audioPattern.matcher(parseResult);

        Long duration = 0L;
        Integer videoBitrate = 0;
        String videoFormat = getFormat(videoFile);
        Long videoSize = videoFile.length();

        String videoEncoder = "";
        Integer videoHeight = 0;
        Integer videoWidth = 0;
        Float videoFramerate = 0F;

        String musicFormat = "";
        Long samplerate = 0L;
        Integer musicBitrate = 0;

        try {
            if (durationMacher.find()) {
                long hours = (long)Integer.parseInt(durationMacher.group(1));
                long minutes = (long)Integer.parseInt(durationMacher.group(2));
                long seconds = (long)Integer.parseInt(durationMacher.group(3));
                long dec = (long)Integer.parseInt(durationMacher.group(4));
                duration = dec * 100L + seconds * 1000L + minutes * 60L * 1000L + hours * 60L * 60L * 1000L;
                //String startTime = durationMacher.group(5) + "ms";
                videoBitrate = Integer.parseInt(durationMacher.group(6));
            }

            if (videoStreamMacher.find()) {
                videoEncoder = videoStreamMacher.group(1);
                String s2 = videoStreamMacher.group(2);
                videoWidth = Integer.parseInt(videoStreamMacher.group(3));
                videoHeight = Integer.parseInt(videoStreamMacher.group(4));
                String s5 = videoStreamMacher.group(5);
                videoFramerate = Float.parseFloat(videoStreamMacher.group(6));
            }

            if (videoMusicStreamMacher.find()) {
                musicFormat = videoMusicStreamMacher.group(1);
                //String s2 = videoMusicStreamMacher.group(2);
                samplerate = Long.parseLong(videoMusicStreamMacher.group(3));
                //String s4 = videoMusicStreamMacher.group(4);
                //String s5 = videoMusicStreamMacher.group(5);
                musicBitrate = Integer.parseInt(videoMusicStreamMacher.group(6));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        VideoMetaData videoMetaInfo = new VideoMetaData();

        videoMetaInfo.setAudioFormat(musicFormat);
        videoMetaInfo.setAudioDuration(duration);
        videoMetaInfo.setAudioBitRate(musicBitrate);
        videoMetaInfo.setSampleRate(samplerate);

        videoMetaInfo.setVideoFormat(videoFormat);
        videoMetaInfo.setVideoSize(videoSize);
        videoMetaInfo.setVideoBitrate(videoBitrate);
        videoMetaInfo.setDuration(duration);
        videoMetaInfo.setVideoEncoder(videoEncoder);
        videoMetaInfo.setVideoFramerate(videoFramerate);
        videoMetaInfo.setVideoHeight(videoHeight);
        videoMetaInfo.setVideoWidth(videoWidth);

        return videoMetaInfo;
    }



    public static String getMetaDataFromFFmpeg(File inputFile) {
        if (inputFile == null || !inputFile.exists()) {
            throw new RuntimeException("File is not exist");
        }
        List<String> commond = new ArrayList<String>();
        commond.add("-i");
        commond.add(inputFile.getAbsolutePath());
        String executeResult = VideoUtil.execute(commond);
        return executeResult;
    }

    private static String getFormat(File file) {
        String fileName = file.getName();
        String format = fileName.substring(fileName.indexOf(".") + 1);
        return format;
    }

    private static class StopProcess extends Thread {
        private Process process;

        public StopProcess(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            this.process.destroy();
        }
    }

    static class PrintingStream extends Thread {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = new StringBuffer();

        public PrintingStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != bufferedReader) {
                        bufferedReader.close();
                    }
                    if (null != inputStream) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
