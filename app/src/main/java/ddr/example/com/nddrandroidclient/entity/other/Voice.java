package ddr.example.com.nddrandroidclient.entity.other;

public class Voice {
    private String text;
    private int interval;
    private int priority;
    private int type;
    private boolean isclose;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isIsclose() {
        return isclose;
    }

    public void setIsclose(boolean isclose) {
        this.isclose = isclose;
    }
}
