package com.animsh.trace;

public class UploadModel {
    private String fileUrl;
    private String name;
    private String fileKey;
    private String date;

    public UploadModel() {
    }

    public UploadModel(String fileUrl, String name, String fileKey, String date) {
        this.fileUrl = fileUrl;
        this.name = name;
        this.fileKey = fileKey;
        this.date = date;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
