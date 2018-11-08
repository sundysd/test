package challenge.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        {
         @JsonSubTypes.Type(value=Text.class, name="text"),
                @JsonSubTypes.Type(value=Image.class, name="image"),
                @JsonSubTypes.Type(value=Video.class, name="video")
        }
)
public abstract class Content {

    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Content(String type){
        this.type = type;
    }

    public Content(){

    }

}
