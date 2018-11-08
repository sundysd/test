package challenge.model;


import java.sql.Timestamp;
import java.util.Date;


public class MessageResponse {

    long id;
    Date timestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
