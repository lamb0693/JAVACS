package com.example.receiver;

public interface DownloadCallback {
    void onDownloadComplete(boolean success, String content);
}
