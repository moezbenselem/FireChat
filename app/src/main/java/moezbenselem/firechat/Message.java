package moezbenselem.firechat;

/**
 * Created by Moez on 04/08/2018.
 */

public class Message {

    private String message,type,from;
    private Boolean seen;
    private long time;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Message() {
    }

    public Message(String message, String type, Boolean seen, long time,String from) {
        this.message = message;
        this.type = type;
        this.seen = seen;
        this.time = time;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
