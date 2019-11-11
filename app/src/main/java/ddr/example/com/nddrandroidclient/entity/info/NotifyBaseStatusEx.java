package ddr.example.com.nddrandroidclient.entity.info;

public class NotifyBaseStatusEx {
    public static NotifyBaseStatusEx notifyBaseStatusEx;


    public static NotifyBaseStatusEx getInstance(){
        if (notifyBaseStatusEx==null){
            synchronized (NotifyBaseStatusEx.class){
                if (notifyBaseStatusEx==null){
                    notifyBaseStatusEx=new NotifyBaseStatusEx();
                }
            }
        }
        return notifyBaseStatusEx;
    }

    private int mode;
    private int sonMode;
    private int eDynamicOAStatus;
    private int stopStat;
    private String curroute="";
    private String currpath="";
    private float posX;
    private float posY;
    private float posDirection;
    private float posLinespeed;
    private float currspeed;
    private int eSelfCalibStatus;
    private boolean chargingStatus;
    private int taskCount;
    private int taskDuration;

    public boolean isChargingStatus() {
        return chargingStatus;
    }

    public int geteDynamicOAStatus() {
        return eDynamicOAStatus;
    }

    public void seteDynamicOAStatus(int eDynamicOAStatus) {
        this.eDynamicOAStatus = eDynamicOAStatus;
    }
    public int geteSelfCalibStatus() {
        return eSelfCalibStatus;
    }

    public void seteSelfCalibStatus(int eSelfCalibStatus) {
        this.eSelfCalibStatus = eSelfCalibStatus;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getTaskDuration() {
        return taskDuration;
    }

    public void setTaskDuration(int taskDuration) {
        this.taskDuration = taskDuration;
    }

    private float posAngulauspeed;




    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getSonMode() {
        return sonMode;
    }

    public void setSonMode(int sonMode) {
        this.sonMode = sonMode;
    }

    public int getStopStat() {
        return stopStat;
    }

    public void setStopStat(int stopStat) {
        this.stopStat = stopStat;
    }

    public String getCurroute() {
        return curroute;
    }

    public void setCurroute(String curroute) {
        this.curroute = curroute;
    }

    public String getCurrpath() {
        return currpath;
    }

    public void setCurrpath(String currpath) {
        this.currpath = currpath;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getPosDirection() {
        return posDirection;
    }

    public void setPosDirection(float posDirection) {
        this.posDirection = posDirection;
    }

    public float getPosLinespeed() {
        return posLinespeed;
    }

    public void setPosLinespeed(float posLinespeed) {
        this.posLinespeed = posLinespeed;
    }

    public float getPosAngulauspeed() {
        return posAngulauspeed;
    }

    public float getCurrspeed(){return currspeed;}

    public void setCurrspeed(float currspeed){ this.currspeed=currspeed;}

    public void setPosAngulauspeed(float posAngulauspeed) {
        this.posAngulauspeed = posAngulauspeed;
    }

    public void setChargingStatus(boolean chargingStatus) {
        this.chargingStatus = chargingStatus;
    }

    public void setNull(){
        notifyBaseStatusEx=null;
    }

}