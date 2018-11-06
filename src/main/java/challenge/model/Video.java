package challenge.model;

enum Source {
    youtube,
    vimeo;
}

public class Video extends Content {

    String url;
    Source source;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }


}