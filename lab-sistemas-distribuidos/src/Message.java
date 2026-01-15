package src;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int senderId;
    private String content;
    private long physicalTimestamp;
    private int lamportTimestamp;
    private int[] vectorTimestamp;
    
    public Message(int senderId, String content) {
        this.senderId = senderId;
        this.content = content;
        this.physicalTimestamp = System.currentTimeMillis();
        this.lamportTimestamp = 0;
        this.vectorTimestamp = null;
    }
    
    public int getSenderId() { return senderId; }
    public String getContent() { return content; }
    public long getPhysicalTimestamp() { return physicalTimestamp; }
    public void setPhysicalTimestamp(long ts) { this.physicalTimestamp = ts; }
    public int getLamportTimestamp() { return lamportTimestamp; }
    public void setLamportTimestamp(int ts) { this.lamportTimestamp = ts; }
    public int[] getVectorTimestamp() { return vectorTimestamp; }
    public void setVectorTimestamp(int[] ts) { this.vectorTimestamp = ts; }
    
    @Override
    public String toString() {
        return "Message{sender=" + senderId + ", content='" + content + "'}";
    }
}
