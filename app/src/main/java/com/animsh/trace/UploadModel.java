package com.animsh.trace;

public class UploadModel {
    private String fileUrl;
    private String name;

    public UploadModel() {
    }

    public UploadModel(String fileUrl, String name) {
        this.fileUrl = fileUrl;
        this.name = name;
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
}
