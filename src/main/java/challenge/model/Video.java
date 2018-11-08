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

    public String getSourceAsString() {
        return source.toString();
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Video(String url, Source source){
        super("video");
        this.url = url;
        this.source = source;
    }

    public Video(){
        super("video");
    }

    public Video(String url, String source){
        super("video");
        this.url = url;
        if ("vimeo".equalsIgnoreCase(source)) {
            this.source = Source.vimeo;
        }  else {
            this.source = Source.youtube;
        }
    }

}