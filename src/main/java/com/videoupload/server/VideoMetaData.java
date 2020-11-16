package com.videoupload.server;

public class VideoMetaData {


    String videoFormat;
    long videoSize;
    int videoBitrate;
    long duration;
    String videoEncoder;
    float videoFramerate;
    int videoHeight;
    int videoWidth;


    String audioFormat;
    long audioDuration;
    int audioBitRate;
    long sampleRate;

    public String getVideoFormat() {
        return videoFormat;
    }

    public void setVideoFormat(String videoFormat) {
        this.videoFormat = videoFormat;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public int getVideoBitrate() {
        return videoBitrate;
    }

    public void setVideoBitrate(int videoBitrate) {
        this.videoBitrate = videoBitrate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getVideoEncoder() {
        return videoEncoder;
    }

    public void setVideoEncoder(String videoEncoder) {
        this.videoEncoder = videoEncoder;
    }

    public float getVideoFramerate() {
        return videoFramerate;
    }

    public void setVideoFramerate(float videoFramerate) {
        this.videoFramerate = videoFramerate;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public String getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(String audioFormat) {
        this.audioFormat = audioFormat;
    }

    public long getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(long audioDuration) {
        this.audioDuration = audioDuration;
    }

    public int getAudioBitRate() {
        return audioBitRate;
    }

    public void setAudioBitRate(int audioBitRate) {
        this.audioBitRate = audioBitRate;
    }

    public long getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(long sampleRate) {
        this.sampleRate = sampleRate;
    }

    @Override
    public String toString() {
        return "VideoMetaData{" +
                "videoFormat='" + videoFormat + '\'' +
                ", videoSize=" + videoSize +
                ", videoBitrate=" + videoBitrate +
                ", duration=" + duration +
                ", videoEncoder='" + videoEncoder + '\'' +
                ", videoFramerate=" + videoFramerate +
                ", videoHeight=" + videoHeight +
                ", videoWidth=" + videoWidth +
                ", audioFormat='" + audioFormat + '\'' +
                ", audioDuration=" + audioDuration +
                ", audioBitRate=" + audioBitRate +
                ", sampleRate=" + sampleRate +
                '}';
    }
}
