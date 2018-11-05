package challenge.model;


import java.net.URI;


public class Image {

    String type;
    URI url;
    int height;
    int width;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
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


}
