package challenge.model;


import java.net.URI;

public class Image extends Content{

    String url;
    int height;
    int width;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Image(String url, int height, int width) {
        super("image");
        this.url = url;
        this.height = height;
        this.width = width;
    }

    public Image() {
        super("image");
    }
}