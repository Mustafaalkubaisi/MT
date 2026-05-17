package com.example.mt_assignment2;

public class AnalysedImage {
    private String id;
    private String imageName;
    private String reader;
    private String text;
    public AnalysedImage(){

    }
    public AnalysedImage(String id, String imageName, String reader, String text) {
        this.id = id;
        this.imageName = imageName;
        this.reader = reader;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getReader() {
        return reader;
    }

    public void setReader(String reader) {
        this.reader = reader;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
